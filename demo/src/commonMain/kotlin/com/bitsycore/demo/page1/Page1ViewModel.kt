package com.bitsycore.demo.page1

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import com.bitsycore.demo.colorpicker.ColorPickerComponent
import com.bitsycore.demo.page1.Page1Contract.Effect
import com.bitsycore.demo.page1.Page1Contract.Intent
import com.bitsycore.demo.page1.Page1Contract.UiState

class Page1ViewModel(savedStateHandle: SavedStateHandle) : Page1Contract.VM(
	containerContract = Page1Contract,
	savedStateHandle = savedStateHandle,
	serializer = UiState.serializer()
) {

	override fun reduce(state: UiState, intent: Intent): UiState = when (intent) {
		Intent.Increment -> state.copy(count = state.count + 1)
		Intent.Decrement -> state.copy(count = state.count - 1)
		Intent.Reset -> state.copy(count = 0)
		is Intent.ColorPicker -> state.copy(
			colorPicker = ColorPickerComponent.reduce(state.colorPicker, intent.intent)
		)
		// Composition: randomize color when entering the screen
		Intent.OnScreenEntered -> state.copy(
			colorPicker = ColorPickerComponent.reduce(state.colorPicker, ColorPickerComponent.Intent.Randomize)
		)
		is Intent.OnLifecycle,
		Intent.OnScreenExited -> state
	}

	override suspend fun handleIntent(intent: Intent) {
		when (intent) {
			Intent.Reset -> emitEffect(Effect.ShowToast("Counter reset!"))

			// Log all lifecycle events
			is Intent.OnLifecycle -> {
				when(intent.event) {
                    Lifecycle.Event.ON_CREATE -> println("[Page1][Lifecycle] onCreate")
                    Lifecycle.Event.ON_START -> {
						emitEffect(Effect.ShowToast("onStart"))
						println("[Page1][Lifecycle] onStart")
					}
                    Lifecycle.Event.ON_RESUME -> println("[Page1][Lifecycle] onResume")
                    Lifecycle.Event.ON_PAUSE -> println("[Page1][Lifecycle] onPause")
                    Lifecycle.Event.ON_STOP -> println("[Page1][Lifecycle] onStop")
                    Lifecycle.Event.ON_DESTROY -> println("[Page1][Lifecycle] onDestroy")
                    else -> {}
                }

            }

			// Log all composition events
			Intent.OnScreenEntered -> println("[Page1][Composition] onEnter")
			Intent.OnScreenExited -> println("[Page1][Composition] onExit")

			else -> {}
		}
	}

	override fun onCleared() = println("[Page1][ViewModel] onCleared")
}
