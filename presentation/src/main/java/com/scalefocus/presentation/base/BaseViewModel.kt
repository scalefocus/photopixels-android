package com.scalefocus.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<ScreenState : Any, Actions, Events>(initialState: ScreenState) : ViewModel() {
    private val mutableState: MutableStateFlow<ScreenState> = MutableStateFlow(initialState)
    private val actions = MutableSharedFlow<Actions>()
    private val eventsChannel = Channel<Events>(Channel.BUFFERED)

    init {
        collectActions()
    }

    val state: StateFlow<ScreenState> = mutableState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STATE_COROUTINE_STOP_DELAY),
        initialValue = initialState
    )

    val events = eventsChannel.receiveAsFlow()

    open suspend fun handleActions(action: Actions) {
        // To or not to override, some screens mights not have actions
    }

    val submitAction: (action: Actions) -> Unit = {
        viewModelScope.launch { actions.emit(it) }
    }

    protected fun submitEvent(event: Events) {
        viewModelScope.launch {
            eventsChannel.send(event)
        }
    }

    protected fun updateState(function: ScreenState.() -> ScreenState) {
        mutableState.update(function)
    }

    private fun collectActions() = viewModelScope.launch {
        actions.collect { handleActions(it) }
    }

    companion object {
        private const val STATE_COROUTINE_STOP_DELAY = 5000L
    }
}
