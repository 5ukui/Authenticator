package com.sukui.authr.ui.screen.account

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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sukui.authr.R
import com.sukui.authr.domain.account.model.DomainAccountInfo
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
    onDismiss: () -> Unit
) {
    val viewModel: AccountViewModel = koinViewModel {
        parametersOf(AccountViewModelParams.Prefilled(prefilled))
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val insets = WindowInsets.navigationBars

    LaunchedEffect(prefilled) {
        viewModel.updateState(prefilled)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp)
                .windowInsetsPadding(insets)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    when (state) {
                        is AccountScreenState.Loading -> {
                            AccountScreenLoading()
                        }
                        is AccountScreenState.Success -> {
                            AccountScreenSuccess(
                                info = (state as AccountScreenState.Success).info,
                                onIconChange = viewModel::updateIcon,
                                onLabelChange = viewModel::updateLabel,
                                onIssuerChange = viewModel::updateIssuer,
                                onSecretChange = viewModel::updateSecret,
                                onTypeChange = viewModel::updateType,
                                onDigestChange = viewModel::updateDigest,
                                onDigitsChange = viewModel::updateDigits,
                                onCounterChange = viewModel::updateCounter,
                                onPeriodChange = viewModel::updatePeriod
                            )
                        }
                        is AccountScreenState.Error -> {
                            AccountScreenError()
                        }
                    }
                }
                Button(
                    onClick = {
                        viewModel.saveData()
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
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
    }
}



@Composable
fun EditAccountScreen(
    id: UUID? = null,
    prefilled: DomainAccountInfo? = null,
    onDismiss: () -> Unit
) {
    val viewModel: AccountViewModel = koinViewModel {
        parametersOf(
            when {
                id != null -> AccountViewModelParams.Id(id)
                prefilled != null -> AccountViewModelParams.Prefilled(prefilled)
                else -> throw IllegalArgumentException("Either id or prefilled must be provided.")
            }
        )
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val canSave by viewModel.canSave.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val insets = WindowInsets.navigationBars

    LaunchedEffect(id, prefilled) {
        when {
            id != null -> viewModel.reloadAccount(id)
            prefilled != null -> viewModel.updateState(prefilled)
        }
    }

    LaunchedEffect(state) {
        if (state is AccountScreenState.Success) {
            sheetState.show()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp)
                .windowInsetsPadding(insets)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    when (state) {
                        is AccountScreenState.Loading -> {
                            AccountScreenLoading()
                        }

                        is AccountScreenState.Success -> {
                            AccountScreenSuccess(
                                info = (state as AccountScreenState.Success).info,
                                onIconChange = viewModel::updateIcon,
                                onLabelChange = viewModel::updateLabel,
                                onIssuerChange = viewModel::updateIssuer,
                                onSecretChange = viewModel::updateSecret,
                                onTypeChange = viewModel::updateType,
                                onDigestChange = viewModel::updateDigest,
                                onDigitsChange = viewModel::updateDigits,
                                onCounterChange = viewModel::updateCounter,
                                onPeriodChange = viewModel::updatePeriod
                            )
                        }

                        is AccountScreenState.Error -> {
                            AccountScreenError()
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.saveData()
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
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
    }
}