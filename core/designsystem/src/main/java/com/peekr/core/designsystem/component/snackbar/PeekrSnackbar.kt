package com.peekr.core.designsystem.component.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Peekr 스낵바
 *
 * @param snackBarHostState SnackbarHostState
 * @param dismissSnackbarState [SwipeToDismissBoxState]
 * @param dismissEnabled SwipeToDismiss 활성화 여부
 *
 * ### 사용 예시
 *    val snackbarHostState = remember { SnackbarHostState() }
 *    val dismissSnackbarState = rememberSwipeToDismissBoxState(
 *        confirmValueChange = { value ->
 *            if (value != SwipeToDismissBoxValue.Settled) {
 *                snackbarHostState.currentSnackbarData?.dismiss()
 *                true
 *            } else {
 *                false
 *            }
 *        }
 *    )
 *
 *    LaunchedEffect(dismissSnackbarState.currentValue) {
 *        if (dismissSnackbarState.currentValue != SwipeToDismissBoxValue.Settled) {
 *            dismissSnackbarState.reset()
 *        }
 *    }
 *
 *    Scaffold(
 *        modifier = Modifier.fillMaxSize(),
 *        snackbarHost = {
 *            PeekrSnackbar(
 *                snackBarHostState = snackbarHostState,
 *                dismissSnackbarState = dismissSnackbarState,
 *            )
 *        }
 *    ) { ... }
 */
@Composable
fun PeekrSnackbar(
    snackBarHostState: SnackbarHostState,
    dismissSnackbarState: SwipeToDismissBoxState,
    dismissEnabled: Boolean = true,
) {
    SwipeToDismissBox(
        state = dismissSnackbarState,
        backgroundContent = {},
        enableDismissFromEndToStart = dismissEnabled,
        enableDismissFromStartToEnd = dismissEnabled,
        content = {
            SnackbarHost(
                modifier = Modifier.imePadding(),
                hostState = snackBarHostState,
            ) { snackbarData ->
                CustomSnackbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .heightIn(min = SnackbarMinHeightDp),
                    snackbarData = snackbarData,
                )
            }
        },
    )
}

@Composable
private fun CustomSnackbar(
    modifier: Modifier = Modifier,
    snackbarData: SnackbarData,
) {
    Snackbar(
        modifier = modifier,
        containerColor = PeekrTheme.colorScheme.textNormal,
        contentColor = PeekrTheme.colorScheme.backgroundNormal,
        shape = RoundedCornerShape(PeekrTheme.shape.large),
        action = {
            snackbarData.visuals.actionLabel?.let { actionLabel ->
                ActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End),
                    text = actionLabel,
                    onClick = { snackbarData.performAction() },
                )
            }
        },
    ) {
        Content(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentWidth(Alignment.Start),
            text = snackbarData.visuals.message,
        )
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickableSingle(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = PeekrTheme.typography.body3Normal,
            fontWeight = FontWeight.Bold,
            color = PeekrTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    text: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = PeekrTheme.typography.body3Normal,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.staticWhite,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val SnackbarMinHeightDp = 56.dp

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun CustomSnackbarPreview() {
    val fakeSnackbarData = object : SnackbarData {
        override val visuals: SnackbarVisuals = object : SnackbarVisuals {
            override val message: String = "이건 스낵바 메시지 예시입니다."
            override val actionLabel: String? = "확인"
            override val withDismissAction: Boolean = true
            override val duration: SnackbarDuration = SnackbarDuration.Short
        }

        override fun dismiss() {}

        override fun performAction() {}
    }

    PeekrAppTheme {
        CustomSnackbar(
            modifier = Modifier.width(300.dp),
            snackbarData = fakeSnackbarData,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PeekrSnackbarPreview() {
    val coroutineScope = rememberCoroutineScope()
    var snackbarJob: Job? by remember { mutableStateOf(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val dismissSnackbarState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value != SwipeToDismissBoxValue.Settled) {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    true
                } else {
                    false
                }
            },
        )

    LaunchedEffect(dismissSnackbarState.currentValue) {
        if (dismissSnackbarState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissSnackbarState.reset()
        }
    }

    PeekrAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                PeekrSnackbar(
                    snackBarHostState = snackbarHostState,
                    dismissSnackbarState = dismissSnackbarState,
                )
            },
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        snackbarJob?.cancel()
                        snackbarJob =
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "네트워크 상태를 확인 해 주세요.",
                                    actionLabel = "action",
                                )
                            }
                    },
                ) {
                    Text(text = "Show Snackbar")
                }

                Text(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                    text = "asdasdljalskdjlaksdjlaks",
                )
            }
        }
    }
}
