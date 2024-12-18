package com.sukui.authr.core.otp.parser

interface OtpUriParser {
    fun parseOtpUri(keyUri: String): OtpUriParserResult
}