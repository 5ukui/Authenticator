package com.sukui.authenticator.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sukui.authenticator.domain.AuthRepository
import com.sukui.authenticator.domain.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settings: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val secureMode = settings.getSecureMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val pinLock = authRepository.observeIsProtected()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val biometrics = settings.getUseBiometrics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun updateSecureMode(newSecureMode: Boolean) {
        viewModelScope.launch {
            settings.setSecureMode(newSecureMode)
        }
    }

    fun toggleBiometrics() {
        viewModelScope.launch {
            settings.setUseBiometrics(!biometrics.value)
        }
    }
}