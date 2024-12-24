package com.sukui.authenticator.ui.screen.qrscan

import androidx.lifecycle.ViewModel
import com.sukui.authenticator.domain.account.model.DomainAccountInfo
import com.sukui.authenticator.domain.otp.OtpRepository

class QrScanViewModel(
    private val repository: OtpRepository
) : ViewModel() {

    fun parseResult(result: com.google.zxing.Result): DomainAccountInfo? {
        return repository.parseUriToAccountInfo(result.text)
    }
}