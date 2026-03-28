package com.bitsycore.lib.pulse.container

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

interface ContainerHost<STATE : Any, INTENT : Any, EFFECT : Any> {
	val stateFlow: StateFlow<STATE>
	val effectFlow: Flow<EFFECT>
	fun dispatch(intent: INTENT)
	fun dispatchDebounced(intent: INTENT, delay: Duration, key: String? = null, skipIfUnchanged: Boolean = false, shareAcrossTypes: Boolean = false)
}