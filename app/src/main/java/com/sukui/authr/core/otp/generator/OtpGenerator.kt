package com.sukui.authr.core.otp.generator

import com.sukui.authr.core.otp.model.OtpDigest

interface OtpGenerator {

    fun generateHotp(
        secret: ByteArray,
        counter: Long,
        digits: Int = 6,
        digest: OtpDigest = OtpDigest.SHA1
    ): String

    fun generateTotp(
        secret: ByteArray,
        interval: Long,
        seconds: Long,
        digits: Int = 6,
        digest: OtpDigest = OtpDigest.SHA1
    ): String

}
