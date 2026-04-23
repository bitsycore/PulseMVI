package com.bitsycore.lib.pulse.savedstate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bitsycore.lib.pulse.container.ContainerContract
import com.bitsycore.lib.pulse.viewmodel.PulseViewModel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * A [PulseViewModel] that automatically persists and restores [STATE] via [SavedStateHandle].
 *
 * On creation, if a previously saved state exists in [savedStateHandle], it is deserialized
 * and used as the initial state (overriding [initialState]).
 *
 * On every state change, the new state is serialized to JSON and written to [savedStateHandle],
 * ensuring survival across process death and backstack eviction.
 *
 * Requires [STATE] to be `@Serializable`.
 *
 * Usage:
 * ```kotlin
 * class MyViewModel(savedStateHandle: SavedStateHandle) :
 *     PulseSavedStateViewModel<UiState, Intent, Effect>(
 *         containerContract = MyContract,
 *         initialState = UiState(),
 *         savedStateHandle = savedStateHandle,
 *         serializer = UiState.serializer()
 *     )
 * ```
 */
abstract class PulseSavedStateViewModel<STATE : Any, INTENT : Any, EFFECT : Any>(
	containerContract: ContainerContract<STATE, INTENT, EFFECT>,
	initialState: STATE,
	private val savedStateHandle: SavedStateHandle,
	private val serializer: KSerializer<STATE>,
	private val savedStateKey: String = "PulseSavedStateViewModel::${containerContract::class}"
) : PulseViewModel<STATE, INTENT, EFFECT>(
	containerContract = containerContract,
	initialState = initialState,
	restoredState = savedStateHandle.get<String>(savedStateKey)?.let { json ->
		runCatching { Json.decodeFromString(serializer, json) }.getOrNull()
	}
) {
	init {
		viewModelScope.launch {
			stateFlow.drop(1).collect { state ->
				savedStateHandle[savedStateKey] = Json.encodeToString(serializer, state)
			}
		}
	}
}