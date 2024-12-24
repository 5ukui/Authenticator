package com.sukui.authenticator.core.otp.parser

interface OtpUriParser {
    fun parseOtpUri(keyUri: String): OtpUriParserResult
}