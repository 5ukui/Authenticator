package com.sukui.authr.ui.screen.export

import androidx.compose.runtime.Immutable
import com.sukui.authr.domain.account.model.DomainExportAccount

@Immutable
sealed interface ExportScreenState {

    @Immutable
    data object Loading : ExportScreenState

    @Immutable
    data class Success(val accounts: List<DomainExportAccount>) : ExportScreenState

    @Immutable
    data object Error : ExportScreenState

}