package com.peekr.core.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.peekr.core.designsystem.component.loading.PeekrLoadingIndicator
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingleWithoutRipple

/**
 * Modal Wrapper
 *
 * 다이얼로그 형태의 모달창을 사용하기 위해 사용한다.
 *
 * ```
 * /** Usage */
 * var isOpen by remember { mutableStateOf(false) }
 *
 * Box(Modifier.fillMaxSize()) {
 *     Button(onClick = { isOpen = true }) { Text("Open modal!") }
 *
 *     PeekrModalWrapper(isOpen = isOpen, onDismissRequest = { isOpen = false }) {
 *     // 모달 컨텐츠
 *         Box(
 *             Modifier
 *                 .fillMaxWidth()
 *                 .height(200.dp)
 *                 .background(Color.LightGray),
 *         )
 *     }
 * }
 * ```
 *
 * @param isOpen 모달 활성화 여부
 * @param animated 모달 애니메이션 활성화 여부
 * @param onDismissRequest 모달이 사라질 때 수행할 작업
 * @param onAnimationFinished 모달이 사라지는 애니메이션이 끝난 직후 수행할 작업
 * @param content 모달 컨텐츠
 */
@Composable
internal fun PeekrModalWrapper(
    isOpen: Boolean,
    animated: Boolean = true,
    loading: Boolean = false,
    onDismissRequest: () -> Unit,
    onAnimationFinished: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    if (isOpen) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ModalScrim(onDismissRequest = onDismissRequest)
                ModalContent(
                    modifier = Modifier
                        .padding(20.dp)
                        .imePadding(),
                    content = content,
                )
                if (loading) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clickableSingleWithoutRipple {},
                    ) {
                        PeekrLoadingIndicator(Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalScrim(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    Box(
        modifier
            .pointerInput(Unit) { detectTapGestures { onDismissRequest() } }
            .fillMaxSize(),
    )
}

@Composable
private fun ModalContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) { detectTapGestures { /* 이벤트 소비 - 스크림으로 전파 방지 */ } }
            .clip(ModalShape)
            .background(PeekrTheme.colorScheme.backgroundNormal, ModalShape)
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 30.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private val ModalShape = RoundedCornerShape(25.dp)
