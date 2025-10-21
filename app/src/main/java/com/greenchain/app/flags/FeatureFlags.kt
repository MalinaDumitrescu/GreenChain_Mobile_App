package com.greenchain.app.flags

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Simple in-app feature-flag system.
 * Later this can be wired to Firebase Remote Config.
 */
object FeatureFlags {

    // Example: toggle RoboFlow AI detection on/off
    private val _useRoboFlow = MutableStateFlow(true)
    val useRoboFlow: StateFlow<Boolean> = _useRoboFlow

    fun setUseRoboFlow(enabled: Boolean) {
        _useRoboFlow.value = enabled
    }
}
