package com.sukui.authenticator.ui.component.pinboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sukui.authenticator.R
import com.sukui.authenticator.ui.theme.MauthTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PinBoard(
    modifier: Modifier = Modifier,
    state: PinBoardState = rememberPinBoardState()
) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        maxItemsInEachRow = 3,
    ) {
        state.buttons.forEach { button ->
            val screenWidth = LocalConfiguration.current.screenWidthDp
            val buttonSize = (screenWidth / 5.2).dp

            when (button) {
                is PinBoardState.PinBoardButton.Number -> {
                    PinButton(
                        modifier = Modifier
                            .size(buttonSize),
                        onClick = { state.onNumberClick(button.number) }
                    ) {
                        Text(
                            text = button.toString(),
                            fontSize = 25.sp
                        )
                    }
                }
                is PinBoardState.PinBoardButton.Backspace,
                is PinBoardState.PinBoardButton.Fingerprint,
                is PinBoardState.PinBoardButton.Enter -> {
                    PrimaryPinButton(
                        modifier = Modifier
                            .size(buttonSize),
                        onClick = when (button) {
                            is PinBoardState.PinBoardButton.Backspace -> state.onBackspaceClick
                            is PinBoardState.PinBoardButton.Fingerprint -> state.onFingerprintClick
                            is PinBoardState.PinBoardButton.Enter -> state.onEnterClick
                            else -> throw NoSuchElementException()
                        },
                        onLongClick =
                            if (button is PinBoardState.PinBoardButton.Backspace)
                                state.onBackspaceLongClick
                            else null
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(0.4f).aspectRatio(1f),
                            painter = painterResource(
                                id = when (button) {
                                    is PinBoardState.PinBoardButton.Backspace -> R.drawable.ic_backspace
                                    is PinBoardState.PinBoardButton.Fingerprint -> R.drawable.ic_fingerprint
                                    is PinBoardState.PinBoardButton.Enter -> R.drawable.ic_tab
                                    else -> throw NoSuchElementException()
                                }
                            ),
                            contentDescription = null
                        )
                    }
                }
                is PinBoardState.PinBoardButton.Empty -> {
                    Spacer(Modifier.size(buttonSize))
                }
            }
        }
    }
}

@Composable
fun rememberPinBoardState(
    showFingerprint: Boolean = false,
    showEnter: Boolean = false,
    onNumberClick: (Char) -> Unit = {},
    onBackspaceClick: () -> Unit = {},
    onBackspaceLongClick: () -> Unit = {},
    onEnterClick: () -> Unit = {},
    onFingerprintClick: () -> Unit = {},
): PinBoardState {
    return remember(
        showFingerprint,
        showEnter,
        onNumberClick,
        onBackspaceClick,
        onBackspaceLongClick,
        onEnterClick,
        onFingerprintClick,
    ) {
        PinBoardState(
            showFingerprint = showFingerprint,
            showEnter = showEnter,
            onNumberClick = onNumberClick,
            onBackspaceClick = onBackspaceClick,
            onBackspaceLongClick = onBackspaceLongClick,
            onEnterClick = onEnterClick,
            onFingerprintClick = onFingerprintClick
        )
    }
}

@Immutable
data class PinBoardState(
    val showFingerprint: Boolean,
    val showEnter: Boolean,
    val onNumberClick: (Char) -> Unit,
    val onBackspaceClick: () -> Unit,
    val onBackspaceLongClick: () -> Unit = {},
    val onEnterClick: () -> Unit,
    val onFingerprintClick: () -> Unit,
) {

    val buttons = buildList {
        ('1'..'9').forEach {
            add(PinBoardButton.Number(it))
        }

        if (showFingerprint) {
            add(PinBoardButton.Fingerprint)
        } else if (showEnter) {
            add(PinBoardButton.Backspace)
        } else {
            add(PinBoardButton.Empty)
        }

        add(PinBoardButton.Number('0'))

        if (showEnter) {
            add(PinBoardButton.Enter)
        } else {
            add(PinBoardButton.Backspace)
        }
    }

    sealed interface PinBoardButton {

        @JvmInline
        value class Number(val number: Char) : PinBoardButton {
            override fun toString() = number.toString()
        }

        data object Fingerprint : PinBoardButton
        data object Backspace : PinBoardButton
        data object Enter : PinBoardButton
        data object Empty : PinBoardButton
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PinBoardPreview_Plain() {
    MauthTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PinBoard(
                state = rememberPinBoardState(),
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PinBoardPreview_WithFingerprint() {
    MauthTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PinBoard(
                state = rememberPinBoardState(showFingerprint = true),
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PinBoardPreview_WithEnter() {
    MauthTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PinBoard(
                state = rememberPinBoardState(showEnter = true),
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PinBoardPreview_WithFingerprintAndEnter() {
    MauthTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PinBoard(
                state = rememberPinBoardState(
                    showFingerprint = true,
                    showEnter = true,
                ),
            )
        }
    }
}