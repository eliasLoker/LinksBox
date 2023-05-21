package com.example.linksbox.features.addfolder

import com.example.linksbox.base.BaseAction
import com.example.linksbox.base.BaseViewEffect
import com.example.linksbox.base.BaseViewState

class AddFolderContract {

    data class ViewState(
        val isLoading: Boolean = true,
        val hasError: Boolean = false
    ) : BaseViewState {

        companion object {
            fun initialState() = ViewState()
        }
    }

    sealed class Action : BaseAction {

        object Loading : Action()

        object ShowScreen : Action()

        object Error : Action()
    }

    sealed class ViewEffect : BaseViewEffect {

        object GoToFolders : ViewEffect()

        object ShowToast : ViewEffect()
    }
}