package com.sukui.authenticator.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sukui.authenticator.R

enum class HomeAddAccountMenu(
    @DrawableRes val icon: Int,
    @StringRes val title: Int
) {
    ScanQR(R.drawable.ic_qr_code_scanner, R.string.home_addaccount_data_scanqr),
    ImageQR(R.drawable.ic_qr_code_2, R.string.home_addaccount_data_imageqr),
    Manual(R.drawable.ic_password, R.string.home_addaccount_data_manual)
}