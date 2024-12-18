package com.sukui.authr.core.auth

import kotlinx.coroutines.flow.Flow

interface AuthManager {

    fun getCode(): Flow<String?>

    fun setCode(code: String)

    fun removeCode()

}