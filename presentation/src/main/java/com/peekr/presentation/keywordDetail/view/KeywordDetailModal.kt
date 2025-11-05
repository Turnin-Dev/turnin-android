package com.peekr.presentation.keywordDetail.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
 * 키워드 상세정보 모달 프레임
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param onDismissRequest 모달이 사라질 때 수행할 작업
 * @param keyword 키워드 명
 * @param description 키워드 내용
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDetailModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    myKeyword: Boolean,
    keyword: String,
    description: String,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    PeekrModalBottomSheet(
        modifier = modifier.statusBarsPadding(),
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        sheetGesturesEnabled = false,
    ) { contentModifier ->
        KeywordDetailModalFrame(
            modifier = contentModifier,
            title = {
                Title(
                    modifier = Modifier.fillMaxWidth(),
                    keyword = keyword,
                    onCancel = onCancel,
                )
            },
            description = {
                DescriptionTab(
                    modifier = Modifier.fillMaxWidth(),
                    description = description,
                )
            },
            otherUsers = {
                OtherUsers()
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
    description: @Composable ColumnScope.() -> Unit,
    otherUsers: @Composable ColumnScope.() -> Unit,
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
            tabs = listOf(
                stringResource(R.string.keyword_detail_modal_tab_bar_title_1),
                stringResource(R.string.keyword_detail_modal_tab_bar_title_2),
            ),
            pageContent = { page ->
                // 키워드 내용
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.size(14.dp))
                    when (page) {
                        0 -> description()
                        1 -> otherUsers()
                    }
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
            contentDescription = stringResource(R.string.keyword_detail_modal_desc_cancel),
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
 * 키워드 내용 탭
 *
 * @param modifier [Modifier]
 * @param description 키워드 내용
 */
@Composable
private fun ColumnScope.DescriptionTab(
    modifier: Modifier = Modifier,
    description: String,
) {
    if (description.isNotEmpty()) {
        Text(
            modifier = modifier,
            text = description,
            style = PeekrTheme.typography.body3Normal,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
            textAlign = TextAlign.Start,
        )
    } else {
        Text(
            modifier = modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.keyword_detail_modal_desc_if_empty),
            style = PeekrTheme.typography.body1,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.interactionInactive,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 다른 사람들(같은 키워드를 사용중인 다른 사용자들) 탭
 */
@Composable
private fun ColumnScope.OtherUsers(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.align(Alignment.CenterHorizontally),
        text = "구현 예정",
        style = PeekrTheme.typography.headline1,
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
private fun KeywordDetailModalPreview() {
    var isOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    PeekrAppTheme {
        Box(Modifier.fillMaxSize()) {
            Button(onClick = { isOpen = true }) { Text(text = "open") }

            if (isOpen) {
                KeywordDetailModal(
                    modifier = Modifier,
                    sheetState = sheetState,
                    myKeyword = true,
                    keyword = "Sample Keyword",
                    description = "이 키워드는 내가 최근에 가장 관심이 많은 어쩌구 저쩌구".repeat(50),
                    onCancel = { isOpen = false },
                    onDismissRequest = { isOpen = false },
                )
            }
        }
    }
}
