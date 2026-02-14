package com.peekr.core.presentation.ui.component.modal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.modal.ModalContentToken
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheetContent
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.ui.util.UiText

/**
 * 선택 가능한 신고/차단 사유 모델
 *
 * [SelectReportBlockModal]에서 사용하는 UI 모델은 반드시 해당 인터페이스를 구현해서 사용한다.
 */
interface SelectableReason {
    val description: String
}

/**
 * 신고/차단 사유 선택 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param reasons 사유 목록
 * @param loading 사유 목록 로딩 여부
 * @param error 에러 메시지
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onCancel 모달 취소 시
 * @param onReasonClick 사유 클릭 시
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : SelectableReason> SelectReportBlockModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    reasons: List<T>,
    loading: Boolean,
    error: UiText?,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onReasonClick: (T) -> Unit,
) {
    PeekrModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        val reasonTokens = reasons.map { reason ->
            ModalContentToken(
                reason.description,
                PeekrTheme.colorScheme.textNormal,
                { onReasonClick(reason) },
            )
        }

        when {
            error != null -> {
                Error(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 270.dp),
                    error = error.asString(),
                )
            }

            loading && reasons.isEmpty() -> {
                ModalContentSkeleton(contentModifier)
            }

            else -> {
                PeekrModalBottomSheetContent(
                    modifier = contentModifier.fillMaxWidth(),
                    onCancel = onCancel,
                    *reasonTokens.toTypedArray(),
                )
            }
        }
    }
}

/**
 * 모달 내 컨텐츠 스켈레톤
 */
@Composable
private fun ModalContentSkeleton(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        listOf(87, 100, 97, 83, 83, 30).forEach { width ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                contentAlignment = Alignment.Center,
            ) {
                SkeletonBox(Modifier.size(width.dp, 18.dp))
            }
        }
    }
}

/**
 * 에러 화면
 */
@Composable
private fun Error(
    modifier: Modifier = Modifier,
    error: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = error,
            style = PeekrTheme.typography.body4,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
        )
    }
}
