package com.example.linksbox.features.links

import android.graphics.Bitmap
import com.example.linksbox.base.BaseAction
import com.example.linksbox.base.BaseViewEffect
import com.example.linksbox.base.BaseViewState
import com.example.linksbox.features.main.DialogData

class LinksContract {

    data class LinkEntityWithBitmap(
        val id: Long,
        var folderId: Long,
        var url: String,
        var title: String,
        var description: String,
        var bitmap: Bitmap?
    )

    data class ViewState (
        val isLoading: Boolean = true,
        val links: List<LinkEntityWithBitmap> = emptyList(),
        val failureOrExceptionMessage: String = "",
        val deleteDialog: DialogData = DialogData(),
        val openBrowserDialog: DialogData = DialogData()
    ) : BaseViewState {

        companion object {

            fun initial() = ViewState(
                isLoading = true,
                links = emptyList(),
                failureOrExceptionMessage = ""
            )
        }
    }

    sealed class Action : BaseAction {

        object Loading : Action()

        class Success(
            val links: List<LinkEntityWithBitmap>
        ) : Action()

        class Failure(
            val failureOrExceptionMessage: String
        ) : Action()

        class ShowDeleteDialog(
            val dialogData: DialogData
        ) : Action()

        class ShowOpenBrowserDialog(
            val dialogData: DialogData
        ) : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        class OpenUrlInBrowse(
            val url: String
        ) : ViewEffect()

        class GoToAddLink(
            val linkId: Long
        ) : ViewEffect()

        class ShowToast(
            val message: String
        ) : ViewEffect()
    }
}