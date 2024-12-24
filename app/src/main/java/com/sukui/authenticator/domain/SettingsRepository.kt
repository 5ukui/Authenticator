package com.sukui.authenticator.domain

import com.sukui.authenticator.core.settings.Settings

class SettingsRepository(private val settings: Settings) : Settings by settings