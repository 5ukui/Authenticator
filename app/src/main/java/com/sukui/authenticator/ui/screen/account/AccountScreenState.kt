package com.sukui.authenticator.ui.screen.account

import androidx.compose.runtime.Immutable
import com.sukui.authenticator.domain.account.model.DomainAccountInfo

@Immutable
sealed interface AccountScreenState {

    @Immutable
    data object Loading : AccountScreenState

    @Immutable
    data class Success(val info: DomainAccountInfo) : AccountScreenState

    @Immutable
    data class Error(val error: String) : AccountScreenState

}