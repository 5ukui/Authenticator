package com.sukui.authenticator.core.auth

import kotlinx.coroutines.flow.Flow

interface AuthManager {

    fun getCode(): Flow<String?>

    fun setCode(code: String)

    fun removeCode()

}