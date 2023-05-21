package com.example.linksbox.features.addlink

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.linksbox.base.BaseViewModel
import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.features.addlink.AddLinkContract.*
import com.example.linksbox.features.main.DialogData
import com.example.linksbox.utils.previewmanager.PreviewManager
import com.example.linksbox.utils.stringprovider.StringProvider
import com.example.linksbox.utils.stringprovider.StringRes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddLinkViewModel(
    private val addLinkInteractor: AddLinkInteractor,
    private val previewManager: PreviewManager,
    private val stringProvider: StringProvider
) : BaseViewModel<ViewState, Action, ViewEffect>(ViewState.initial()) {

    val link = mutableStateOf("")
    val linkTitle = mutableStateOf("")
    val linkDescription = mutableStateOf("")

    private var bitmap: Bitmap? = null
    private var linkId: Long? = null

    override fun onReduceState(viewAction: Action): ViewState {
        return when (viewAction) {
            is Action.Loading -> {
                state.value.copy(
                    isLoading = true
                )
            }
            is Action.Success -> {
                state.value.copy(
                    isLoading = false,
                    previewData = viewAction.previewData
                )
            }
            is Action.ShowSelectFolderDialog -> {
                state.value.copy(
                    selectFolderDialog = viewAction.dialogSelectFolderData
                )
            }

            is Action.Error -> {
                state.value.copy(hasError = true)
            }

            is Action.ShowCreateFolderDialog -> {
                state.value.copy(
                    createFolderDialog = viewAction.dialogData
                )
            }
        }
    }

    override fun handleException(throwable: Throwable) {
        sendAction(Action.Error)
    }

    fun fetchLinkInfo(url: String?, linkId: Long?) {
        this.linkId = linkId
        baseViewModelScope.launch {
            if (url == null && linkId == null) {
                handleException(IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE))
            } else if (!url.isNullOrEmpty()) {
                val linkItem = addLinkInteractor.getLinkItemFromNetwork(url)
                    .also {
                        link.value = it.url
                        linkTitle.value = it.title ?: ""
                        linkDescription.value = it.description ?: ""
                    }
                sendAction(
                    Action.Success(
                        previewData = PreviewData(
                            imageUrl = linkItem.imageUrl
                        )
                    )
                )
            } else if (linkId != null) {
                val linkItem = addLinkInteractor.getLinkById(linkId)
                    .also {
                        link.value = it.url
                        linkTitle.value = it.title
                        linkDescription.value = it.description
                    }
                val bitmap = previewManager.getPreviewFromInternalStorage(linkItem.imageUrl)
                sendAction(
                    Action.Success(
                        previewData = PreviewData(
                            bitmap = bitmap
                        )
                    )
                )
            }
        }
    }

    fun bitmapLoaded(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    fun onSaveClicked() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(
                stringProvider.getStringByStringRes(
                    if (linkId != null) StringRes.ERROR_UPDATE_PREVIEW else StringRes.ERROR_SAVE_PREVIEW
                )
            )
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            linkId?.let {
                addLinkInteractor.updateLinkById(
                    linkId = it,
                    title = linkTitle.value,
                    description = linkDescription.value
                )
                sendEffect(ViewEffect.GoToLinks(it))
            } ?: addLinkInteractor.getFolders().collectLatest {
                sendAction(
                    Action.ShowSelectFolderDialog(
                        DialogSelectFolderData(
                            isShowDialog = true,
                            folders = it
                        )
                    )
                )
            }
        }
    }

    fun onDialogFolderSelectedClicked(folderId: Long) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(stringProvider.getStringByStringRes(StringRes.ERROR_SAVE_PREVIEW))
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            val writtenFileName = previewManager.downloadPreviewToInternalStorage(bitmap!!)
            addLinkInteractor.insertLink(
                folderId = folderId,
                url = link.value,
                title = linkTitle.value,
                description = linkDescription.value,
                imageUrl = writtenFileName
            )
            sendActionHideSelectFolderDialog()
            sendEffect(
                ViewEffect.GoToLinksWithClearBackStack(
                    folderId = folderId
                )
            )
        }
    }

    fun onDialogDismissClicked() {
        sendActionHideSelectFolderDialog()
    }

    fun onAddFolderDialogClicked() {
        sendActionHideSelectFolderDialog()
        sendAction(Action.ShowCreateFolderDialog(
            DialogData(
                isShowDialog = true,
                title = stringProvider.getStringByStringRes(StringRes.CREATE_FOLDER),
                positiveButton = stringProvider.getStringByStringRes(StringRes.CREATE),
                negativeButton = stringProvider.getStringByStringRes(StringRes.CANCEL)
            )
        ))
    }

    fun onCreateFolderDialogClicked(title: String, description: String) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            sendEffectShowToast(stringProvider.getStringByStringRes(StringRes.ERROR_SAVE_PREVIEW))
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            val folderId = addLinkInteractor.insertFolder(
                FolderEntity(
                    title,
                    description
                )
            )
            val writtenFileName = previewManager.downloadPreviewToInternalStorage(bitmap!!)
            addLinkInteractor.insertLink(
                folderId,
                url = link.value,
                title = linkTitle.value,
                description = linkDescription.value,
                imageUrl = writtenFileName
            )
            sendActionHideCreateFolderDialog()
            sendEffect(
                ViewEffect.GoToLinksWithClearBackStack(
                    folderId = folderId
                )
            )
        }
    }

    fun onDialogCreateFolderDismissClicked() {
        sendActionHideCreateFolderDialog()
    }

    fun onDispose() {
        bitmap = null
        linkId = null
    }

    private fun sendActionHideCreateFolderDialog() {
        sendAction(Action.ShowCreateFolderDialog(
            DialogData(
                isShowDialog = false,
            )
        ))
    }

    private fun sendActionHideSelectFolderDialog() {
        sendAction(
            Action.ShowSelectFolderDialog(
                DialogSelectFolderData(
                    isShowDialog = false,
                    folders = null
                )
            )
        )
    }

    private fun sendEffectShowToast(message: String) {
        sendEffect(ViewEffect.ShowToast(message))
    }

    companion object {
        private const val ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE = "Id and link must not be null"
    }
}