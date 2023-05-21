package com.example.linksbox.features.main

import com.example.linksbox.base.BaseViewModel
import com.example.linksbox.features.main.MainContract.*

class MainViewModel : BaseViewModel<ViewState, Action, ViewEffect>(ViewState()) {

    override fun onReduceState(viewAction: Action): ViewState = ViewState()

    override fun handleException(throwable: Throwable) { /* Do nothing */ }

    fun onNewIntent(link: String?) {
        link?.let {
            sendEffect(
                ViewEffect.OpenAddLinkScreen(it)
            )
        }
    }
}