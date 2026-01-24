package com.peekr.presentation.report.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.modal.ModalContentToken
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheetContent
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.domain.report.model.ReportReasonId
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.presentation.report.model.UiReportReason

/**
 * 신고 사유 선택 모달
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param reportReasons 신고 사유 목록
 * @param loading 신고 사유 목록 로딩 여부
 * @param error 에러 메시지
 * @param onDismissRequest 모달이 사라질 때 수행할 콜백
 * @param onCancel 모달 취소 시
 * @param onReportReasonsClick 신고 사유 클릭 시
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectReportReasonModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    reportReasons: List<UiReportReason>,
    loading: Boolean,
    error: UiText?,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onReportReasonsClick: (UiReportReason) -> Unit,
) {
    PeekrModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        onDismissRequest = onDismissRequest,
    ) { contentModifier ->
        val reasonTokens = reportReasons.map { reason ->
            ModalContentToken(
                reason.description,
                PeekrTheme.colorScheme.textNormal,
                { onReportReasonsClick(reason) },
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

            loading && reportReasons.isEmpty() -> {
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

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun SelectReportReasonModalPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        SelectReportReasonModal(
            sheetState = sheetState,
            reportReasons = listOf(
                UiReportReason(
                    id = ReportReasonId(1L),
                    code = "SPAM",
                    description = "스팸 및 사기",
                ),
                UiReportReason(
                    id = ReportReasonId(1L),
                    code = "INAPPROPRIATE",
                    description = "부적절한 콘텐츠",
                ),
                UiReportReason(
                    id = ReportReasonId(1L),
                    code = "ETC",
                    description = "기타",
                ),
            ),
            loading = false,
            error = null,
            onDismissRequest = {},
            onCancel = {},
            onReportReasonsClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun SkeletonPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        SelectReportReasonModal(
            sheetState = sheetState,
            reportReasons = emptyList(),
            loading = true,
            error = null,
            onDismissRequest = {},
            onCancel = {},
            onReportReasonsClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ErrorPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        SelectReportReasonModal(
            sheetState = sheetState,
            reportReasons = emptyList(),
            loading = false,
            error = UiText.DynamicString("잠시 오류가 발생했어요. 다시 시도해주세요."),
            onDismissRequest = {},
            onCancel = {},
            onReportReasonsClick = {},
        )
    }
}
