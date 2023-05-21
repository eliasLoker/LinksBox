package com.example.linksbox.features.addfolder

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.linksbox.base.BaseViewModel
import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.features.addfolder.AddFolderContract.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AddFolderViewModel(
    private val addFolderInteractor: AddFolderInteractor
) : BaseViewModel<ViewState, Action, ViewEffect>(ViewState.initialState()) {

    val folderNameValue = mutableStateOf("")
    val folderDescriptionValue = mutableStateOf("")

    private var folderId: Long? = null

    override fun onReduceState(viewAction: Action): ViewState {
        return when (viewAction) {

            is Action.Loading -> {
                state.value.copy(
                    isLoading = true
                )
            }

            is Action.ShowScreen -> {
                state.value.copy(
                    isLoading = false
                )
            }

            is Action.Error -> {
                state.value.copy(
                    hasError = true
                )
            }
        }
    }

    override fun handleException(throwable: Throwable) {
        sendAction(Action.Error)
    }

    fun fetchFolder(folderId: Long?) {
        this.folderId = folderId
        if (folderId == null) {
            sendAction(Action.ShowScreen)
        } else {
            baseViewModelScope.launch {
                val folderItem = addFolderInteractor.getFolderById(folderId)
                folderNameValue.value = folderItem.name
                folderDescriptionValue.value = folderItem.description ?: ""
                sendAction(Action.ShowScreen)
            }
        }
    }

    fun onSaveButtonClicked() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            sendEffect(ViewEffect.ShowToast)
        }
        viewModelScope.launch(baseDispatcher + coroutineExceptionHandler) {
            if (folderId != null) {
                addFolderInteractor.updateProjectById(
                    folderId = folderId!!,
                    name = folderNameValue.value,
                    description = folderDescriptionValue.value
                )
                sendEffect(ViewEffect.GoToFolders)
            } else {
                addFolderInteractor.insertFolder(
                    FolderEntity(
                        name = folderNameValue.value,
                        description = folderDescriptionValue.value
                    )
                )
                sendEffect(ViewEffect.GoToFolders)
            }
        }
    }

    fun onDispose() {
        folderId = null
        folderNameValue.value = ""
        folderDescriptionValue.value = ""
    }
}