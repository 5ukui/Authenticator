package com.sukui.authr.core.otp.exporter

import com.sukui.authr.core.otp.model.OtpData

interface OtpExporter {

    fun exportOtp(data: OtpData): String

}