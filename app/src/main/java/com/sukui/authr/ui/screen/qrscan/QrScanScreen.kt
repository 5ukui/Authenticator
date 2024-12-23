package com.sukui.authr.ui.screen.qrscan

import android.Manifest
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.sukui.authr.R
import com.sukui.authr.domain.account.model.DomainAccountInfo
import com.sukui.authr.ui.screen.qrscan.component.QrScanPermissionDeniedDialog
import com.sukui.authr.ui.screen.qrscan.state.QrScanPermissionDenied
import com.sukui.authr.ui.screen.qrscan.state.QrScanPermissionGranted
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun QrScanScreen(
    onDismiss: () -> Unit,
    onScan: (DomainAccountInfo) -> Unit
) {
    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val viewModel: QrScanViewModel = koinViewModel()
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialogRationale by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(cameraPermission.status) {
        if (cameraPermission.status is PermissionStatus.Denied) {
            showPermissionDeniedDialog = true
            showPermissionDeniedDialogRationale = (cameraPermission.status as PermissionStatus.Denied).shouldShowRationale
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                contentAlignment = Alignment.Center
            ) {
                when (cameraPermission.status) {
                    is PermissionStatus.Granted -> {
                        QrScanPermissionGranted(
                            onScan = { result ->
                                viewModel.parseResult(result)?.let { parsedInfo ->
                                    onScan(parsedInfo)
                                }
                                coroutineScope.launch {
                                    sheetState.hide()
                                    onDismiss()
                                }
                            }
                        )
                    }

                    is PermissionStatus.Denied -> {
                        QrScanPermissionDenied()
                    }
                }
            }
        }
    }

    if (showPermissionDeniedDialog) {
        QrScanPermissionDeniedDialog(
            shouldShowRationale = showPermissionDeniedDialogRationale,
            onGrantPermission = {
                showPermissionDeniedDialog = false
                cameraPermission.launchPermissionRequest()
            },
            onCancel = {
                showPermissionDeniedDialog = false
            }
        )
    }
}

