package com.sukui.authenticator.ui.screen.home

import androidx.compose.runtime.Immutable
import com.sukui.authenticator.domain.account.model.DomainAccount

@Immutable
sealed interface HomeScreenState {

    @Immutable
    data object Loading : HomeScreenState

    @Immutable
    data object Empty : HomeScreenState

    @Immutable
    @JvmInline
    value class Success(val accounts: List<DomainAccount>) : HomeScreenState

    @Immutable
    @JvmInline
    value class Error(val error: String) : HomeScreenState

}