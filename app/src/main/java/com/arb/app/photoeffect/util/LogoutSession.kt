package com.arb.app.photoeffect.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LogoutSession {
    private val _errorMessages = MutableStateFlow<String?>(null)
    val errorMessages: StateFlow<String?> = _errorMessages

    fun clearError() {
        _errorMessages.value = null
    }
}