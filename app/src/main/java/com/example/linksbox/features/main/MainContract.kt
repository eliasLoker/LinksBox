package com.example.linksbox.features.main

import com.example.linksbox.base.BaseAction
import com.example.linksbox.base.BaseViewEffect
import com.example.linksbox.base.BaseViewState

class MainContract {

    data class ViewState(
        val isLoading: Boolean = true
    ) : BaseViewState

    sealed class Action : BaseAction { /* Do nothing */ }

    sealed class ViewEffect : BaseViewEffect {

        class OpenAddLinkScreen(
            val link: String
        ) : ViewEffect()
    }
}