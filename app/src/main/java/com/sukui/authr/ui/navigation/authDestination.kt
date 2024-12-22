package com.sukui.authr.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

sealed class authDestination(val isFullscreenDialog: Boolean = false) : Parcelable {

    @Parcelize
    data class Auth(val nextDestination: authDestination? = null) : authDestination()

    @Parcelize
    data object Home : authDestination()

    @Parcelize
    data object QrScanner : authDestination()

    @Parcelize
    data object Settings : authDestination()

    @Parcelize
    data class Export(
        // Empty list means export all
        val accounts: List<UUID> = emptyList()
    ) : authDestination()

    @Parcelize
    data object PinSetup : authDestination()

    @Parcelize
    data object PinRemove : authDestination()

    @Parcelize
    data object Theme : authDestination()

    @Parcelize
    data object About : authDestination()
}