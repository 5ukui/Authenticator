package com.sukui.authenticator.ui.screen.export

import androidx.compose.runtime.Immutable
import com.sukui.authenticator.domain.account.model.DomainExportAccount

@Immutable
sealed interface ExportScreenState {

    @Immutable
    data object Loading : ExportScreenState

    @Immutable
    data class Success(val accounts: List<DomainExportAccount>) : ExportScreenState

    @Immutable
    data object Error : ExportScreenState

}