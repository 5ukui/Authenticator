package com.sukui.authenticator.core.otp.parser

import com.sukui.authenticator.core.otp.model.OtpData

sealed interface OtpUriParserResult {
    data class Success(val data: OtpData) : OtpUriParserResult
    data class Failure(val error: OtpUriParserError) : OtpUriParserResult
}