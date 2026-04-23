package com.bitsycore.lib.pulse.container

/**
 * Groups the three MVI types for a screen into a single object.
 *
 * Each screen contract should be an `object` that:
 *  - Extends `ContainerContract<STATE, INTENT, EFFECT>`
 *  - Declares nested `UiState`, `Intent`, and `Effect`
 *  - Optionally overrides [reduce] for state transitions
 *
 * Usage:
 * ```
 * object MyContract : ContainerContract<MyContract.UiState, MyContract.Intent, MyContract.Effect>() {
 *     data class UiState(...)
 *     sealed interface Intent { ... }
 *     sealed interface Effect { ... }
 * }
 *
 * class MyViewModel : PulseViewModel<MyContract.UiState, MyContract.Intent, MyContract.Effect>(MyContract, MyContract.UiState()) { ... }
 * ```
 */
abstract class ContainerContract<STATE : Any, INTENT : Any, EFFECT : Any> {
	open fun reduce(state: STATE, intent: INTENT): STATE = state
}