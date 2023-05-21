package com.example.linksbox.base

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel <
        ViewState : BaseViewState,
        ViewAction: BaseAction,
        ViewEffect: BaseViewEffect
    >(initialState: ViewState) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }
    protected val baseDispatcher = Dispatchers.Main
    protected val baseViewModelScope = CoroutineScope(baseDispatcher + coroutineExceptionHandler)

    private val _state = MutableStateFlow<ViewState>(initialState)
    val state = _state.asStateFlow()

    private val _effect = MutableStateFlow<BaseViewEffect?>(null)
    val effect: StateFlow<BaseViewEffect?> = _effect.asStateFlow()

    protected abstract fun onReduceState(viewAction: ViewAction) : ViewState

    protected fun sendAction(viewAction: ViewAction) {
        _state.value = onReduceState(viewAction)
    }

    protected fun sendEffect(viewEffect: ViewEffect) {
        _effect.value = viewEffect
    }

    protected abstract fun handleException(throwable: Throwable)

    fun clearEffect() {
        _effect.value = null
    }
}

@Immutable
interface BaseViewState

interface BaseAction

interface BaseViewEffect