package com.sukui.authenticator.ui.screen.home.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sukui.authenticator.domain.account.model.DomainAccount
import com.sukui.authenticator.domain.otp.model.DomainOtpRealtimeData
import com.sukui.authenticator.ui.screen.home.component.HomeAccountCard
import java.util.UUID

@Composable
fun HomeScreenSuccess(
    onAccountSelect: (UUID) -> Unit,
    onAccountEdit: (UUID) -> Unit,
    onAccountCounterIncrease: (UUID) -> Unit,
    onAccountCopyCode: (String, String, Boolean) -> Unit,
    accounts: List<DomainAccount>,
    selectedAccounts: List<UUID>,
    accountRealtimeData: Map<UUID, DomainOtpRealtimeData>,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        columns = GridCells.Adaptive(minSize = 250.dp),
    ) {
        items(
            items = accounts,
            key = { it.id }
        ) { account ->
            val realtimeData = accountRealtimeData[account.id]
            if (realtimeData != null) {
                HomeAccountCard(
                    onClick = {
                        if (selectedAccounts.isNotEmpty()) {
                            onAccountSelect(account.id)
                        }
                    },
                    onLongClick = {
                        onAccountSelect(account.id)
                    },
                    onEdit = {
                        onAccountEdit(account.id)
                    },
                    onCounterClick = {
                        onAccountCounterIncrease(account.id)
                    },
                    onCopyCode = {
                        onAccountCopyCode(account.label, realtimeData.code, it)
                    },
                    account = account,
                    realtimeData = realtimeData,
                    selected = selectedAccounts.contains(account.id)
                )
            }
        }
    }
}