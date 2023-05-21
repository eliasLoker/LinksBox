package com.example.linksbox.features.links

import androidx.lifecycle.viewModelScope
import com.example.linksbox.base.BaseViewModel
import com.example.linksbox.features.folders.FoldersViewModel
import com.example.linksbox.features.links.LinksContract.*
import com.example.linksbox.features.main.DialogData
import com.example.linksbox.utils.previewmanager.PreviewManager
import com.example.linksbox.utils.stringprovider.StringProvider
import com.example.linksbox.utils.stringprovider.StringRes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LinksViewModel(
    private val previewManager: PreviewManager,
    private val stringProvider: StringProvider,
    private val linksInteractor: LinksInteractor
) : BaseViewModel<ViewState, Action, ViewEffect>(ViewState.initial()) {

    override fun onReduceState(viewAction: Action): ViewState {
        return when (viewAction) {
            is Action.Loading -> {
                state.value.copy(
                    isLoading = true,
                    links = emptyList()
                )
            }
            is Action.Success -> {
                state.value.copy(
                    isLoading = false,
                    links = viewAction.links
                )
            }
            is Action.Failure -> {
                state.value.copy(
                    isLoading = false,
                    links = emptyList(),
                    failureOrExceptionMessage = viewAction.failureOrExceptionMessage
                )
            }
            is Action.ShowDeleteDialog -> {
                state.value.copy(
                    deleteDialog = viewAction.dialogData
                )
            }
            is Action.ShowOpenBrowserDialog -> {
                state.value.copy(
                    openBrowserDialog = viewAction.dialogData
                )
            }
        }
    }

    override fun handleException(throwable: Throwable) {
        sendAction(Action.Failure(
            stringProvider.getStringByStringRes(StringRes.ERROR_GETTING_LINKS)
        ))
    }

    fun fetchLinks(id: Long) {
        baseViewModelScope.launch {
            linksInteractor.getLinks(id).collectLatest {
                if (it.isEmpty()) {
                    sendAction(
                        Action.Failure(
                            stringProvider.getStringByStringRes(StringRes.EMPTY_LINKS)
                        )
                    )
                } else {
                    val linkEntities = it.map {
                        val bitmap = previewManager.getPreviewFromInternalStorage(it.imageUrl)
                        LinkEntityWithBitmap(
                            id = it.id,
                            folderId = it.folderId,
                            url = it.url,
                            title = it.title,
                            description = it.description,
                            bitmap = bitmap
                        )
                    }
                    sendAction(
                        Action.Success(
                            links = linkEntities
                        )
                    )
                }
            }
        }
    }

    fun onDeleteButtonClicked(id: Long) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(stringProvider.getStringByStringRes(StringRes.ERROR_DELETE_LINK))
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            val item = linksInteractor.getLinkById(id)
            sendAction(
                Action.ShowDeleteDialog(
                    DialogData(
                        itemId = item.id,
                        isShowDialog = true,
                        title = stringProvider.getStringByStringRes(StringRes.DELETE),
                        subtitle = stringProvider.getStringByStringRes(
                            StringRes.DIALOG_DELETE_LINK_MESSAGE,
                            item.title
                        ),
                        negativeButton = stringProvider.getStringByStringRes(StringRes.CANCEL),
                        positiveButton = stringProvider.getStringByStringRes(StringRes.DELETE)
                    )
                )
            )
        }
    }

    fun onDeleteDialogDismissClicked() {
        sendActionHideDeleteDialog()
    }

    fun onDeleteDialogConfirmClicked(id: Long?) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(stringProvider.getStringByStringRes(StringRes.ERROR_DELETE_LINK))
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            id?.let {
                val item = linksInteractor.getLinkById(it)
                previewManager.deletePreview(item.imageUrl)
                linksInteractor.deleteLinkById(it)
            } ?: throw IllegalArgumentException(FoldersViewModel.ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE)
            sendActionHideDeleteDialog()
        }
    }

    fun onOpenBrowserButtonClicked(id: Long) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(stringProvider.getStringByStringRes(StringRes.ERROR_OPEN_LINK))
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            val item = linksInteractor.getLinkById(id)
            sendAction(Action.ShowOpenBrowserDialog(
                    DialogData(
                        itemId = item.id,
                        isShowDialog = true,
                        title = stringProvider.getStringByStringRes(StringRes.OPEN_IN_BROWSER_TITLE),
                        subtitle = stringProvider.getStringByStringRes(
                            StringRes.OPEN_IN_BROWSER_MESSAGE,
                            item.title
                        ),
                        negativeButton = stringProvider.getStringByStringRes(StringRes.CANCEL),
                        positiveButton = stringProvider.getStringByStringRes(StringRes.OPEN)
                    )
            ))
        }
    }

    fun onOpenBrowserDialogDismissClicked() {
        sendActionHideOpenBrowserDialog()
    }

    fun onConfirmOpenBrowserClicked(id: Long?) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(stringProvider.getStringByStringRes(StringRes.ERROR_OPEN_LINK))
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            id?.let {
                val item = linksInteractor.getLinkById(it)
                sendEffect(
                    ViewEffect.OpenUrlInBrowse(item.url)
                )
                sendActionHideOpenBrowserDialog()
            }
        }
    }

    fun onEditButtonClicked(linkId: Long) {
        sendEffect(
            ViewEffect.GoToAddLink(
                linkId = linkId
            )
        )
    }

    private fun sendEffectShowToast(message: String) {
        sendEffect(ViewEffect.ShowToast(message))
    }

    private fun sendActionHideOpenBrowserDialog() {
        sendAction(
            Action.ShowOpenBrowserDialog(
                DialogData(
                    isShowDialog = false,
                )
            )
        )
    }

    private fun sendActionHideDeleteDialog() {
        sendAction(
            Action.ShowDeleteDialog(
                DialogData(
                    isShowDialog = false,
                )
            )
        )
    }
}