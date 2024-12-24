package com.sukui.authenticator.core.otp.transformer

interface KeyTransformer {

    fun transformToBytes(key: String): ByteArray

}

