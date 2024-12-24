package com.sukui.authenticator.ui.screen.qrscan.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sukui.authenticator.R

@Composable
fun QrScanPermissionDeniedDialog(
    shouldShowRationale: Boolean,
    onGrantPermission: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.qrscan_permissions_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (shouldShowRationale) {
                        stringResource(R.string.qrscan_permissions_subtitle_rationale)
                    } else {
                        stringResource(R.string.qrscan_permissions_subtitle)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onGrantPermission,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.qrscan_permissions_button_grant),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.qrscan_permissions_button_cancel),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
