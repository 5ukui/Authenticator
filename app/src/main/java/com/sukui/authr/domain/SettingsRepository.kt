package com.sukui.authr.domain

import com.sukui.authr.core.settings.Settings

class SettingsRepository(private val settings: Settings) : Settings by settings