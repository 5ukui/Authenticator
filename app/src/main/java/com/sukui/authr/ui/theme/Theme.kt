package com.sukui.authr.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.sukui.authr.core.settings.model.ColorSetting
import com.sukui.authr.core.settings.model.ThemeSetting
import com.sukui.authr.ui.theme.color.BlueberryBlueDark
import com.sukui.authr.ui.theme.color.LimeGreenDark
import com.sukui.authr.ui.theme.color.MothPurpleDark
import com.sukui.authr.ui.theme.color.OrangeOrangeDark
import com.sukui.authr.ui.theme.color.SkyCyanDark
import com.sukui.authr.ui.theme.color.LemonYellowDark
import com.sukui.authr.ui.theme.color.BlueberryBlueLight
import com.sukui.authr.ui.theme.color.LemonYellowLight
import com.sukui.authr.ui.theme.color.LimeGreenLight
import com.sukui.authr.ui.theme.color.MothPurpleLight
import com.sukui.authr.ui.theme.color.OrangeOrangeLight
import com.sukui.authr.ui.theme.color.SkyCyanLight

@Composable
fun MauthTheme(
    theme: ThemeSetting = ThemeSetting.DEFAULT,
    color: ColorSetting = ColorSetting.DEFAULT,
    content: @Composable () -> Unit
) {
    val isDark = when (theme) {
        ThemeSetting.System -> isSystemInDarkTheme()
        ThemeSetting.Dark -> true
        ThemeSetting.Light -> false
    }
    val isInPreview = LocalInspectionMode.current
    val colorScheme = when {
        color == ColorSetting.Dynamic && (isInPreview || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            val context = LocalContext.current
            when (isDark) {
                true -> dynamicDarkColorScheme(context)
                false -> dynamicLightColorScheme(context)
            }
        }
        color == ColorSetting.BlueberryBlue -> when (isDark) {
            true -> BlueberryBlueDark
            false -> BlueberryBlueLight
        }
        color == ColorSetting.PickleYellow -> when (isDark) {
            true -> LemonYellowDark
            false -> LemonYellowLight
        }
        color == ColorSetting.ToxicGreen -> when (isDark) {
            true -> LimeGreenDark
            false -> LimeGreenLight
        }
        color == ColorSetting.LeatherOrange -> when (isDark) {
            true -> OrangeOrangeDark
            false -> OrangeOrangeLight
        }
        color == ColorSetting.OceanTurquoise -> when (isDark) {
            true -> SkyCyanDark
            false -> SkyCyanLight
        }
        else -> when (isDark) {
            true -> MothPurpleDark
            false -> MothPurpleLight
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}