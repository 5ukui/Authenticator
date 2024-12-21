package com.sukui.authr.ui

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sukui.authr.core.settings.model.ColorSetting
import com.sukui.authr.core.settings.model.ThemeSetting
import com.sukui.authr.domain.AuthRepository
import com.sukui.authr.domain.SettingsRepository
import com.sukui.authr.domain.account.model.DomainAccountInfo
import com.sukui.authr.domain.otp.OtpRepository
import com.sukui.authr.ui.navigation.authDestination
import com.sukui.authr.ui.screen.about.AboutScreen
import com.sukui.authr.ui.screen.account.AddAccountScreen
import com.sukui.authr.ui.screen.account.EditAccountScreen
import com.sukui.authr.ui.screen.auth.AuthScreen
import com.sukui.authr.ui.screen.export.ExportScreen
import com.sukui.authr.ui.screen.home.HomeScreen
import com.sukui.authr.ui.screen.pinremove.PinRemoveScreen
import com.sukui.authr.ui.screen.pinsetup.PinSetupScreen
import com.sukui.authr.ui.screen.qrscan.QrScanScreen
import com.sukui.authr.ui.screen.settings.SettingsScreen
import com.sukui.authr.ui.screen.theme.ThemeScreen
import com.sukui.authr.ui.theme.MauthTheme
import com.sukui.authr.util.launchInLifecycle
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import dev.olshevski.navigation.reimagined.replaceLast
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainActivity : FragmentActivity() {

    private val settings: SettingsRepository by inject()
    private val otp: OtpRepository by inject()
    private val auth: AuthRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)


        settings.getTheme()
            .launchInLifecycle(lifecycle) {
                val systemBarStyle = when (it) {
                    ThemeSetting.System -> SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
                    ThemeSetting.Dark -> SystemBarStyle.dark(Color.TRANSPARENT)
                    ThemeSetting.Light -> SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(systemBarStyle, systemBarStyle)
            }

        settings.getSecureMode()
            .launchInLifecycle(lifecycle) {
                if (it) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }


        val initialScreen = runBlocking {
            if (auth.isProtected()) {
                authDestination.Auth()
            } else {
                authDestination.Home
            }
        }

        setContent {
            val theme by settings.getTheme().collectAsStateWithLifecycle(initialValue = ThemeSetting.DEFAULT)
            val color by settings.getColor().collectAsStateWithLifecycle(initialValue = ColorSetting.DEFAULT)
            MauthTheme(
                theme = theme,
                color = color
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigator = rememberNavController(initialScreen)

                    LaunchedEffect(intent.data) {
                        val accountInfo = otp.parseUriToAccountInfo(intent.data.toString())
                        if (accountInfo != null) {
                            navigator.navigate(authDestination.AddAccount(accountInfo))
                        }
                    }

                    AnimatedNavHost(
                        controller = navigator,
                        transitionSpec = { action, initial, target ->
                            when {
                                target.isFullscreenDialog -> {
                                    slideIntoContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    ) togetherWith fadeOut()
                                }
                                initial.isFullscreenDialog -> {
                                    fadeIn() togetherWith slideOutOfContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = spring(
                                            stiffness = Spring.StiffnessVeryLow
                                        )
                                    )
                                }
                                initial is authDestination.Auth && action !is NavAction.Pop -> {
                                    fadeIn() + scaleIn(
                                        initialScale = 0.9f
                                    ) togetherWith fadeOut() + slideOut {
                                        IntOffset(0, -100)
                                    }
                                }
                                else -> when (action) {
                                    NavAction.Navigate -> {
                                        fadeIn() + scaleIn(
                                            initialScale = 0.9f
                                        ) togetherWith fadeOut() + scaleOut(
                                            targetScale = 1.1f
                                        )
                                    }
                                    NavAction.Pop -> {
                                        fadeIn() + scaleIn(
                                            initialScale = 1.1f
                                        ) togetherWith fadeOut() + scaleOut(
                                            targetScale = 0.9f
                                        )
                                    }
                                    else -> fadeIn() togetherWith fadeOut()
                                }
                            }
                        }
                    ) { screen ->
                        when (screen) {
                            is authDestination.Auth -> {
                                AuthScreen(
                                    onAuthSuccess = {
                                        if (screen.nextDestination != null) {
                                            navigator.replaceLast(screen.nextDestination)
                                        } else {
                                            navigator.replaceAll(authDestination.Home)
                                        }
                                    },
                                    onBackPress = if (screen.nextDestination == null) null else { ->
                                        navigator.pop()
                                    }
                                )
                            }
                            is authDestination.Home -> {
                                HomeScreen(
                                    onAddAccountManually = {
                                        navigator.navigate(
                                            authDestination.AddAccount(DomainAccountInfo.new())
                                        )
                                    },
                                    onAddAccountViaScanning = {
                                        navigator.navigate(authDestination.QrScanner)
                                    },
                                    onAddAccountFromImage = {
                                        navigator.navigate(authDestination.AddAccount(it))
                                    },
                                    onSettingsNavigate = {
                                        navigator.navigate(authDestination.Settings)
                                    },
                                    onExportNavigate = { accounts ->
                                        navigator.navigateSecure(authDestination.Export(accounts))
                                    },
                                    onAboutNavigate = {
                                        navigator.navigate(authDestination.About)
                                    }
                                )
                            }
                            is authDestination.QrScanner -> {
                                QrScanScreen(
                                    onBack = navigator::pop,
                                    onScan = {
                                        navigator.replaceLast(authDestination.AddAccount(it))
                                    }
                                )
                            }
                            is authDestination.Settings -> {
                                SettingsScreen(
                                    onBack = navigator::pop,
                                    onSetupPinCode = {
                                        navigator.navigate(authDestination.PinSetup)
                                    },
                                    onDisablePinCode = {
                                        navigator.navigate(authDestination.PinRemove)
                                    },
                                    onThemeNavigate = {
                                        navigator.navigate(authDestination.Theme)
                                    }
                                )
                            }
                            is authDestination.About -> {
                                AboutScreen(onBack = navigator::pop)
                            }
                            is authDestination.AddAccount -> {
                                AddAccountScreen(
                                    prefilled = screen.params,
                                    onExit = navigator::pop
                                )
                            }
                            is authDestination.EditAccount -> {
                                EditAccountScreen(
                                    id = screen.id,
                                    onDismiss = navigator::pop
                                )
                            }
                            is authDestination.PinSetup -> {
                                PinSetupScreen(onExit = navigator::pop)
                            }
                            is authDestination.PinRemove -> {
                                PinRemoveScreen(onExit = navigator::pop)
                            }
                            is authDestination.Theme -> {
                                ThemeScreen(onExit = navigator::pop)
                            }
                            is authDestination.Export -> {
                                ExportScreen(
                                    accounts = screen.accounts,
                                    onBackNavigate = navigator::pop
                                )
                            }
                        }
                    }
                }
            }
        }
    }



    private fun NavController<authDestination>.navigateSecure(destination: authDestination) {
        val isProtected = runBlocking { auth.isProtected() }
        if (isProtected) {
            navigate(authDestination.Auth(nextDestination = destination))
        } else {
            navigate(destination)
        }
    }
}