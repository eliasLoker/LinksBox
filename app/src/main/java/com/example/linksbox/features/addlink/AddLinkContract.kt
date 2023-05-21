package com.example.linksbox.features.addlink

import android.graphics.Bitmap
import com.example.linksbox.base.BaseAction
import com.example.linksbox.base.BaseViewEffect
import com.example.linksbox.base.BaseViewState
import com.example.linksbox.features.main.DialogData

class AddLinkContract {

    data class ViewState(
        val isLoading: Boolean = true,
        val hasError: Boolean = false,
        val previewData: PreviewData = PreviewData(),
        val selectFolderDialog: DialogSelectFolderData = DialogSelectFolderData(),
        val createFolderDialog: DialogData = DialogData()
    ) : BaseViewState {

        companion object {
            fun initial() = ViewState()
        }
    }

    data class PreviewData(
        val bitmap: Bitmap? = null,
        val imageUrl: String? = null
    )

    sealed class Action : BaseAction {

        object Loading : Action()

        class Success(
            val previewData: PreviewData
        ) : Action()

        object Error : Action()

        class ShowSelectFolderDialog(
            val dialogSelectFolderData: DialogSelectFolderData
        ) : Action()

        class ShowCreateFolderDialog(
            val dialogData: DialogData
        ) : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        class GoToLinks(
            val linkId: Long
        ) : ViewEffect()

        class GoToLinksWithClearBackStack(
            val folderId: Long
        ) : ViewEffect()

        class ShowToast(
            val message: String
        ) : ViewEffect()
    }
}