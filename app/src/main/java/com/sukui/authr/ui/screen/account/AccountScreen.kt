package com.sukui.authr.ui.screen.account

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sukui.authr.R
import com.sukui.authr.core.otp.model.OtpDigest
import com.sukui.authr.core.otp.model.OtpType
import com.sukui.authr.domain.account.model.DomainAccountInfo
import com.sukui.authr.ui.screen.account.component.AccountExitDialog
import com.sukui.authr.ui.screen.account.state.AccountScreenError
import com.sukui.authr.ui.screen.account.state.AccountScreenLoading
import com.sukui.authr.ui.screen.account.state.AccountScreenSuccess
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun AddAccountScreen(
    prefilled: DomainAccountInfo,
    onExit: () -> Unit
) {
    val viewModel: AccountViewModel = koinViewModel {
        parametersOf(AccountViewModelParams.Prefilled(prefilled))
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val hasChanges by viewModel.hasChanges.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()
    AccountScreen(
        title = stringResource(R.string.account_title_add),
        state = state,
        hasChanges = hasChanges,
        canSave = canSave,
        onIconChange = viewModel::updateIcon,
        onLabelChange = viewModel::updateLabel,
        onIssuerChange = viewModel::updateIssuer,
        onSecretChange = viewModel::updateSecret,
        onTypeChange = viewModel::updateType,
        onDigestChange = viewModel::updateDigest,
        onDigitsChange = viewModel::updateDigits,
        onCounterChange = viewModel::updateCounter,
        onPeriodChange = viewModel::updatePeriod,
        onSave = {
            viewModel.saveData()
            onExit()
        },
        onExit = onExit
    )
}


@Composable
fun EditAccountScreen(
    id: UUID,
    onDismiss: () -> Unit
) {
    val viewModel: AccountViewModel = koinViewModel {
        parametersOf(AccountViewModelParams.Id(id))
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val insets = WindowInsets.navigationBars

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp)
                .windowInsetsPadding(insets)
        ) {
            AccountBottomSheetContent(
                state = state,
                canSave = canSave,
                onIconChange = viewModel::updateIcon,
                onLabelChange = viewModel::updateLabel,
                onIssuerChange = viewModel::updateIssuer,
                onSecretChange = viewModel::updateSecret,
                onTypeChange = viewModel::updateType,
                onDigestChange = viewModel::updateDigest,
                onDigitsChange = viewModel::updateDigits,
                onCounterChange = viewModel::updateCounter,
                onPeriodChange = viewModel::updatePeriod,
                onSave = {
                    viewModel.saveData()
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )
        }
    }
}

@Composable
fun AccountScreen(
    title: String,
    state: AccountScreenState,
    hasChanges: Boolean,
    canSave: Boolean,
    onIconChange: (Uri?) -> Unit,
    onLabelChange: (String) -> Unit,
    onIssuerChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onTypeChange: (OtpType) -> Unit,
    onDigestChange: (OtpDigest) -> Unit,
    onDigitsChange: (String) -> Unit,
    onCounterChange: (String) -> Unit,
    onPeriodChange: (String) -> Unit,
    onSave: () -> Unit,
    onExit: () -> Unit,
) {
    var isExitDialogShown by remember { mutableStateOf(false) }
    BackHandler {
        if (hasChanges) {
            isExitDialogShown = true
        } else {
            onExit()
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = canSave
                    ) {
                        Text(stringResource(R.string.account_actions_save))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) {
                            isExitDialogShown = true
                        } else {
                            onExit()
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(title)
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (state) {
                is AccountScreenState.Loading -> {
                    AccountScreenLoading()
                }
                is AccountScreenState.Success -> {
                    AccountScreenSuccess(
                        info = state.info,
                        onIconChange = onIconChange,
                        onLabelChange = onLabelChange,
                        onIssuerChange = onIssuerChange,
                        onSecretChange = onSecretChange,
                        onTypeChange = onTypeChange,
                        onDigestChange = onDigestChange,
                        onDigitsChange = onDigitsChange,
                        onCounterChange = onCounterChange,
                        onPeriodChange = onPeriodChange
                    )
                }
                is AccountScreenState.Error -> {
                    AccountScreenError()
                }
            }
        }
    }
    if (isExitDialogShown) {
        AccountExitDialog(
            onCancel = {
                isExitDialogShown = false
            },
            onConfirm = {
                isExitDialogShown = false
                onExit()
            }
        )
    }
}


@Composable
fun AccountBottomSheetContent(
    state: AccountScreenState,
    canSave: Boolean,
    onIconChange: (Uri?) -> Unit,
    onLabelChange: (String) -> Unit,
    onIssuerChange: (String) -> Unit,
    onSecretChange: (String) -> Unit,
    onTypeChange: (OtpType) -> Unit,
    onDigestChange: (OtpDigest) -> Unit,
    onDigitsChange: (String) -> Unit,
    onCounterChange: (String) -> Unit,
    onPeriodChange: (String) -> Unit,
    onSave: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (state) {
                is AccountScreenState.Loading -> {
                    AccountScreenLoading()
                }

                is AccountScreenState.Success -> {
                    AccountScreenSuccess(
                        info = state.info,
                        onIconChange = onIconChange,
                        onLabelChange = onLabelChange,
                        onIssuerChange = onIssuerChange,
                        onSecretChange = onSecretChange,
                        onTypeChange = onTypeChange,
                        onDigestChange = onDigestChange,
                        onDigitsChange = onDigitsChange,
                        onCounterChange = onCounterChange,
                        onPeriodChange = onPeriodChange
                    )
                }

                is AccountScreenState.Error -> {
                    AccountScreenError()
                }
            }
        }

        Button(
            onClick = onSave,
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .zIndex(1f),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = stringResource(R.string.account_actions_save),
            )
        }
    }

}

