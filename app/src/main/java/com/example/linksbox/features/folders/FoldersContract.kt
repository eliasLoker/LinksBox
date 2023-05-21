package com.example.linksbox.features.folders

import com.example.linksbox.base.BaseAction
import com.example.linksbox.base.BaseViewEffect
import com.example.linksbox.base.BaseViewState
import com.example.linksbox.data.entities.FolderEntity
import com.example.linksbox.features.main.DialogData

class FoldersContract {

    data class ViewState(
        val isLoading: Boolean = true,
        val folders: List<FolderEntity> = emptyList(),
        val failureOrExceptionMessage: String = "",
        val editFolderDialog: DialogData = DialogData()
    ) : BaseViewState {

        companion object {
            fun initial() = ViewState(
                isLoading = true,
                folders = emptyList(),
                failureOrExceptionMessage = ""
            )
        }
    }

    sealed class Action : BaseAction {

        object Loading : Action()

        class Success(
            val folders: List<FolderEntity>
        ) : Action()

        class Failure(
            val failureOrExceptionMessage: String
        ) : Action()

        class ShowDialog(
            val dialogData: DialogData
        ) : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        class GoToFolders(
            val folderId: Long
        ) : ViewEffect()

        class ShowToast(val message: String) : ViewEffect()
    }
}