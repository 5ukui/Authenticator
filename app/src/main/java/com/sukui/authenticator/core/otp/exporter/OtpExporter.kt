package com.sukui.authenticator.core.otp.exporter

import com.sukui.authenticator.core.otp.model.OtpData

interface OtpExporter {

    fun exportOtp(data: OtpData): String

}