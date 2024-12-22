package com.sukui.authr.ui.screen.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sukui.authr.domain.account.model.DomainAccountInfo
import com.sukui.authr.ui.screen.account.EditAccountScreen
import com.sukui.authr.ui.screen.home.component.HomeAddAccountSheet
import com.sukui.authr.ui.screen.home.component.HomeDeleteAccountsDialog
import com.sukui.authr.ui.screen.home.component.HomeScaffold
import com.sukui.authr.ui.screen.home.state.HomeScreenEmpty
import com.sukui.authr.ui.screen.home.state.HomeScreenError
import com.sukui.authr.ui.screen.home.state.HomeScreenLoading
import com.sukui.authr.ui.screen.home.state.HomeScreenSuccess
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun HomeScreen(
    onAddAccountManually: () -> Unit,
    onAddAccountViaScanning: () -> Unit,
    onAddAccountFromImage: (DomainAccountInfo) -> Unit,
    onSettingsNavigate: () -> Unit,
    onExportNavigate: (accounts: List<UUID>) -> Unit,
    onAboutNavigate: () -> Unit
) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val realTimeData by viewModel.realTimeData.collectAsStateWithLifecycle()
    val selectedAccounts by viewModel.selectedAccounts.collectAsStateWithLifecycle()
    val activeSortSetting by viewModel.activeSortSetting.collectAsStateWithLifecycle()

    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModel.getAccountInfoFromQrUri(uri)?.let {
            onAddAccountFromImage(it)
        }
    }

    var showAddSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var accountToEdit by remember { mutableStateOf<UUID?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()


    HomeScaffold(
        isSelectionActive = selectedAccounts.isNotEmpty(),
        onAdd = { showAddSheet = true },
        onCancelSelection = viewModel::clearAccountSelection,
        onDeleteSelected = { showDeleteDialog = true },
        onMenuNavigate = {
            when (it) {
                HomeMoreMenu.Settings -> onSettingsNavigate()
                HomeMoreMenu.Export -> onExportNavigate(selectedAccounts)
                HomeMoreMenu.About -> onAboutNavigate()
            }
        },
        activeSortSetting = activeSortSetting,
        onActiveSortChange = viewModel::setActiveSort,
        scrollBehavior = scrollBehavior
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (val currentState = state) {
                is HomeScreenState.Loading -> HomeScreenLoading()
                is HomeScreenState.Empty -> HomeScreenEmpty()
                is HomeScreenState.Success -> HomeScreenSuccess(
                    onAccountSelect = viewModel::toggleAccountSelection,
                    onAccountEdit = { accountId ->
                        if (!showEditSheet) {
                            accountToEdit = accountId
                            showEditSheet = true
                        }
                    },
                    onAccountCounterIncrease = viewModel::incrementCounter,
                    onAccountCopyCode = viewModel::copyCodeToClipboard,
                    accounts = currentState.accounts,
                    selectedAccounts = selectedAccounts,
                    accountRealtimeData = realTimeData
                )
                is HomeScreenState.Error -> HomeScreenError()
            }
        }
    }

    if (showAddSheet) {
        HomeAddAccountSheet(
            onDismiss = { showAddSheet = false },
            onAddAccountNavigate = {
                showAddSheet = false
                when (it) {
                    HomeAddAccountMenu.ScanQR -> onAddAccountViaScanning()
                    HomeAddAccountMenu.ImageQR -> photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                    HomeAddAccountMenu.Manual -> onAddAccountManually()
                }
            }
        )
    }

    if (showEditSheet && accountToEdit != null) {
        EditAccountScreen(
            id = accountToEdit!!,
            onDismiss = {
                showEditSheet = false
            }
        )
    }

    if (showDeleteDialog) {
        HomeDeleteAccountsDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteSelectedAccounts()
            },
            onCancel = { showDeleteDialog = false }
        )
    }
}
