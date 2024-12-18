package com.sukui.authr.core.otp.exporter

import android.net.Uri
import com.sukui.authr.core.otp.model.OtpData
import com.sukui.authr.core.otp.model.OtpType

class DefaultOtpExporter : OtpExporter {

    override fun exportOtp(data: OtpData): String {
        val uriBuilder = Uri.Builder()
            .scheme("otpauth")
            .appendPath(data.label)
            .appendQueryParameter("secret", data.secret)
            .appendQueryParameter("algorithm", data.algorithm.name)
            .appendQueryParameter("digits", data.digits.toString())

        if (data.issuer.isNotBlank()) {
            uriBuilder.appendQueryParameter("issuer", data.issuer)
        }

        return when (data.type) {
            OtpType.TOTP -> {
                uriBuilder
                    .authority("totp")
                    .appendQueryParameter("period", data.period.toString())
            }
            OtpType.HOTP -> {
                uriBuilder
                    .authority("hotp")
                    .appendQueryParameter("counter", data.period.toString())
            }
        }.toString().also(::println)
    }

}