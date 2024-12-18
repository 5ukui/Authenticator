package com.sukui.authr.ui.screen.qrscan

import androidx.lifecycle.ViewModel
import com.sukui.authr.domain.account.model.DomainAccountInfo
import com.sukui.authr.domain.otp.OtpRepository

class QrScanViewModel(
    private val repository: OtpRepository
) : ViewModel() {

    fun parseResult(result: com.google.zxing.Result): DomainAccountInfo? {
        return repository.parseUriToAccountInfo(result.text)
    }
}