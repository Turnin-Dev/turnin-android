package com.peekr.presentation.keywordDetail.view

import android.inputmethodservice.Keyboard.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.component.tabBar.PeekrTabBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Cancel
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.presentation.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R

// TODO: 1. `본인 키워드 조회 시`: 키워드 내용, 같은 키워드를 등록한 다른 사용자 표시
// TODO: 2. `다른 사용자 키워드 조회 시(친구 관계)`: 키워드 내용 표시
// TODO: 3. `다른 사용자 키워드 조회 시(친구 관계 X)`: 아무것도 표시 하지 않음

/**
 * 나의 키워드 상세정보 모달 프레임
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param onDismissRequest 모달이 사라질 때 수행할 작업
 * @param keyword 키워드 명
 * @param description 키워드 내용
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyKeywordDetailModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    keyword: String,
    description: String,
) {
    PeekrModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        sheetGesturesEnabled = false,
    ) { contentModifier ->
        KeywordDetailModalFrame(
            modifier = contentModifier.fillMaxHeight(0.9f),
            title = {
                Title(
                    modifier = Modifier.fillMaxWidth(),
                    keyword = keyword,
                    onCancel = {},
                )
            },
            description = {
                Description(
                    modifier = Modifier.fillMaxWidth(),
                    description = description,
                )
            },
        )
    }
}

/**
 * 키워드 상세정보 모달 프레임
 *
 * @param modifier [Modifier]
 * @param title 최상단 타이틀 (키워드 명)
 * @param description 키워드 내용
 */
@Composable
private fun KeywordDetailModalFrame(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    description: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        // 최상단 타이틀(키워드 명 표시)
        title()
        Spacer(Modifier.size(15.dp))
        // 탭바 & 키워드 내용
        PeekrTabBar(
            modifier = Modifier.fillMaxWidth(),
            tabs = listOf(stringResource(R.string.my_keyword_detail_modal_tab_bar_title)),
            userScrollEnabled = false,
            pageContent = {
                // 키워드 내용
                Box(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    description()
                }
            },
        )
    }
}

/**
 * 최상단 타이틀 (키워드 명)
 *
 * @param modifier [Modifier]
 * @param keyword 키워드 명
 * @param onCancel 취소 클릭 시
 */
@Composable
private fun Title(
    modifier: Modifier = Modifier,
    keyword: String,
    onCancel: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        PeekrIconButton(
            modifier = Modifier.wrapContentWidth(Alignment.Start),
            icon = PeekrIcons.Default.Bold.Cancel,
            iconSize = PeekrIconSize.Normal,
            contentDescription = stringResource(R.string.my_keyword_detail_modal_desc_cancel),
            tint = PeekrTheme.colorScheme.textNormal,
            expandedTouchTarget = false,
            onClick = onCancel,
        )
        Text(
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            text = keyword,
            style = PeekrTheme.typography.headline2,
            fontWeight = FontWeight.Bold,
            color = PeekrTheme.colorScheme.textNormal,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(PeekrIconSize.Normal.size))
    }
}

/**
 * 키워드 내용
 *
 * @param modifier [Modifier]
 * @param description 키워드 내용
 */
@Composable
private fun Description(
    modifier: Modifier = Modifier,
    description: String,
) {
    Text(
        modifier = modifier,
        text = description,
        style = PeekrTheme.typography.body3Normal,
        fontWeight = FontWeight.Normal,
        color = PeekrTheme.colorScheme.textNormal,
    )
}

// ------------------------------ Preview ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun TitlePreview() {
    PeekrAppTheme {
        Title(
            modifier = Modifier.fillMaxWidth(),
            keyword = "키워드",
            onCancel = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun MyKeywordDetailModalPreview() {
    var isOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        Box(Modifier.fillMaxSize()) {
            Button(onClick = { isOpen = true }) { Text(text = "open") }

            if (isOpen) {
                MyKeywordDetailModal(
                    modifier = Modifier,
                    sheetState = sheetState,
                    onDismissRequest = { isOpen = false },
                    keyword = "Sample Keyword",
                    description = "이 키워드는 내가 최근에 가장 관심이 많은 어쩌구 저쩌구".repeat(50),
                )
            }
        }
    }
}
