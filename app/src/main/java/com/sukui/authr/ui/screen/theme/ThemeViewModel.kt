package com.sukui.authr.ui.screen.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sukui.authr.core.settings.model.ColorSetting
import com.sukui.authr.core.settings.model.ThemeSetting
import com.sukui.authr.domain.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme = settingsRepository.getTheme()
        .stateIn(
            scope = viewModelScope,
            initialValue = ThemeSetting.DEFAULT,
            started = SharingStarted.WhileSubscribed(5000)
        )

    val color = settingsRepository.getColor()
        .stateIn(
            scope = viewModelScope,
            initialValue = ColorSetting.DEFAULT,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun updateTheme(newTheme: ThemeSetting) {
        viewModelScope.launch {
            settingsRepository.setTheme(newTheme)
        }
    }

    fun updateColor(newColor: ColorSetting) {
        viewModelScope.launch {
            settingsRepository.setColor(newColor)
        }
    }
}