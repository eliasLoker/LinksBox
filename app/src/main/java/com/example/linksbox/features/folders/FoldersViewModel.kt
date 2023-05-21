package com.example.linksbox.features.folders

import androidx.lifecycle.viewModelScope
import com.example.linksbox.base.BaseViewModel
import com.example.linksbox.features.folders.FoldersContract.*
import com.example.linksbox.features.main.DialogData
import com.example.linksbox.utils.previewmanager.PreviewManager
import com.example.linksbox.utils.stringprovider.StringProvider
import com.example.linksbox.utils.stringprovider.StringRes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FoldersViewModel(
    private val stringProvider: StringProvider,
    private val previewManager: PreviewManager,
    private val foldersInteractor: FoldersInteractor
) : BaseViewModel<ViewState, Action, ViewEffect>(ViewState.initial()) {

    init {
        baseViewModelScope.launch {
            foldersInteractor.getFolders().collectLatest {
                sendAction(
                    if (it.isNotEmpty()) Action.Success(it)  else Action.Failure(stringProvider.getStringByStringRes(StringRes.EMPTY_FOLDERS))
                )
            }
        }
    }

    override fun onReduceState(viewAction: Action): ViewState {
        return when (viewAction) {
            is Action.Loading -> {
                state.value.copy(
                    isLoading = true,
                    folders = emptyList()
                )
            }
            is Action.Success -> {
                state.value.copy(
                    isLoading = false,
                    folders = viewAction.folders
                )
            }
            is Action.Failure -> {
                state.value.copy(
                    isLoading = false,
                    folders = emptyList(),
                    failureOrExceptionMessage = viewAction.failureOrExceptionMessage
                )
            }
            is Action.ShowDialog -> {
                state.value.copy(
                    editFolderDialog = viewAction.dialogData
                )
            }
        }
    }

    override fun handleException(throwable: Throwable) {
        sendAction(
            Action.Failure(
                stringProvider.getStringByStringRes(StringRes.ERROR_GETTING_FOLDERS)
            )
        )
    }

    fun onDeleteButtonClicked(id: Long) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(stringProvider.getStringByStringRes(StringRes.DELETE_FOLDER_ERROR))
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            foldersInteractor.getFolders().collectLatest { it ->
                val item = it.first { it.id == id }
                sendAction(
                    Action.ShowDialog(
                        DialogData(
                            itemId = item.id,
                            isShowDialog = true,
                            title = stringProvider.getStringByStringRes(StringRes.DELETE),
                            subtitle = stringProvider.getStringByStringRes(
                                StringRes.DIALOG_DELETE_FOLDER_MESSAGE,
                                item.name
                            ),
                            negativeButton = stringProvider.getStringByStringRes(StringRes.CANCEL),
                            positiveButton = stringProvider.getStringByStringRes(StringRes.DELETE)
                        )
                    )
                )
            }
        }
    }

    fun onDeleteDialogDismissClicked() {
        sendActionHideDeleteDialog()
    }

    fun onDeleteDialogConfirmClicked(id: Long?) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(stringProvider.getStringByStringRes(StringRes.DELETE_FOLDER_ERROR))
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            id?.let { notNullId ->
                foldersInteractor.getLinks(notNullId).collectLatest { links ->
                    val previewUrls = links.map { it.imageUrl }
                    previewManager.deletePreviewList(previewUrls)
                    foldersInteractor.deleteFolderById(notNullId)
                }
            } ?: throw IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE)
        }
        sendActionHideDeleteDialog()
    }

    private fun sendEffectShowToast(message: String) {
        sendEffect(ViewEffect.ShowToast(message))
    }

    private fun sendActionHideDeleteDialog() {
        sendAction(
            Action.ShowDialog(
                DialogData(
                    isShowDialog = false,
                )
            )
        )
    }

    companion object {
        const val ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE = "Id must not be null"
    }
}