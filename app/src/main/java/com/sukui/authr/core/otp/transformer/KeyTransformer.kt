package com.sukui.authr.core.otp.transformer

interface KeyTransformer {

    fun transformToBytes(key: String): ByteArray

}

