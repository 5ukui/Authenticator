package com.sukui.authr.core.otp.parser

import com.sukui.authr.core.otp.model.OtpData

sealed interface OtpUriParserResult {
    data class Success(val data: OtpData) : OtpUriParserResult
    data class Failure(val error: OtpUriParserError) : OtpUriParserResult
}