package com.peekr.presentation.keywordDetail.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.component.modal.PeekrModalBottomSheet
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.component.tabBar.PeekrTabBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Cancel
import com.peekr.core.designsystem.util.icon.Check
import com.peekr.core.designsystem.util.icon.Edit
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract

/**
 * 키워드 상세정보 모달 프레임
 *
 * @param modifier [Modifier]
 * @param sheetState [SheetState]
 * @param myKeyword 내 키워드 여부
 * @param keyword 키워드 명
 * @param description 키워드 내용
 * @param editMode 수정 모드 활성화 여부
 * @param loading 로딩 여부
 * @param loadingDescription 키워드 설명 로딩 여부
 * @param fullScreenError 전체 화면 에러 여부
 * @param fullScreenErrorMessage 전체 화면 에러 메시지
 * @param onUiEvent UI 이벤트 전달
 * @param onForceCancel 강제 취소 시
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDetailModal(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    myKeyword: Boolean,
    keyword: String,
    description: TextFieldValue,
    editMode: Boolean,
    loading: Boolean,
    loadingDescription: Boolean,
    fullScreenError: Boolean,
    fullScreenErrorMessage: String,
    onUiEvent: (KeywordDetailContract.UiEvent) -> Unit,
    onForceCancel: () -> Unit,
) {
    Box(modifier) {
        PeekrModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = { onUiEvent(KeywordDetailContract.UiEvent.SafeCancel) },
            sheetGesturesEnabled = false,
        ) { contentModifier ->
            KeywordDetailModalFrame(
                modifier = contentModifier,
                myKeyword = myKeyword,
                loadingDescription = loadingDescription,
                isFullScreenError = fullScreenError,
                title = {
                    Title(
                        modifier = Modifier.fillMaxWidth(),
                        myKeyword = myKeyword,
                        editMode = editMode,
                        keyword = keyword,
                        onCancel = { onUiEvent(KeywordDetailContract.UiEvent.SafeCancel) },
                        onEdit = { onUiEvent(KeywordDetailContract.UiEvent.EnableEditMode) },
                        onEditAccept = {
                            onUiEvent(KeywordDetailContract.UiEvent.UpdateDescription(description.text))
                        },
                    )
                },
                otherUserDescriptionTab = {
                    OtherUserDescriptionTab(
                        modifier = Modifier.fillMaxWidth(),
                        description = description.text,
                    )
                },
                myDescriptionTab = {
                    MyDescriptionTab(
                        modifier = Modifier.fillMaxWidth(),
                        description = description,
                        editMode = editMode,
                        onDescriptionChanged = {
                            onUiEvent(
                                KeywordDetailContract.UiEvent.OnDescriptionChanged(value = it),
                            )
                        },
                    )
                },
                otherUsersTab = {
                    OtherUsers()
                },
                fullScreenError = {
                    FullScreenError(
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = fullScreenErrorMessage,
                        onCancel = onForceCancel,
                    )
                },
            )
        }

        if (loading) {
            PeekrLoadingScreen()
        }
    }
}

/**
 * 키워드 상세정보 모달 프레임
 *
 * @param modifier [Modifier]
 * @param myKeyword 내 키워드 여부
 * @param isFullScreenError 전체 화면 에러 여부
 * @param title 최상단 타이틀 (키워드 명)
 * @param otherUserDescriptionTab 다른 사용자 키워드 내용 탭
 * @param myDescriptionTab 내 키워드 내용 탭
 * @param otherUsersTab 다른 사람들 프로필 탭
 * @param fullScreenError 전체 화면 에러
 */
@Composable
private fun KeywordDetailModalFrame(
    modifier: Modifier = Modifier,
    myKeyword: Boolean,
    loadingDescription: Boolean,
    isFullScreenError: Boolean,
    title: @Composable () -> Unit,
    otherUserDescriptionTab: @Composable ColumnScope.() -> Unit,
    myDescriptionTab: @Composable ColumnScope.() -> Unit,
    otherUsersTab: @Composable ColumnScope.() -> Unit,
    fullScreenError: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        if (!isFullScreenError) {
            // 최상단 타이틀(키워드 명 표시)
            title()
            Spacer(Modifier.size(15.dp))
            // 탭바 & 키워드 내용
            PeekrTabBar(
                modifier = Modifier.fillMaxWidth(),
                tabs = if (myKeyword) {
                    listOf(
                        stringResource(R.string.keyword_detail_modal_tab_bar_title_1),
                        stringResource(R.string.keyword_detail_modal_tab_bar_title_2),
                    )
                } else {
                    listOf(stringResource(R.string.keyword_detail_modal_tab_bar_title_1))
                },
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
                        if (loadingDescription) {
                            DescriptionSkeletonScreen()
                        } else {
                            when (page) {
                                0 -> if (myKeyword) myDescriptionTab() else otherUserDescriptionTab()
                                1 -> if (myKeyword) otherUsersTab()
                            }
                        }
                    }
                },
            )
        } else {
            fullScreenError()
        }
    }
}

/**
 * 최상단 타이틀 (키워드 명)
 *
 * @param modifier [Modifier]
 * @param myKeyword 내 키워드 여부
 * @param editMode 수정 모드 허용 여부
 * @param keyword 키워드 명
 * @param onCancel 취소 클릭 시
 * @param onEdit 수정 클릭 시
 * @param onEditAccept 수정 완료 클릭 시
 */
@Composable
private fun Title(
    modifier: Modifier = Modifier,
    myKeyword: Boolean,
    editMode: Boolean,
    keyword: String,
    onCancel: () -> Unit,
    onEdit: () -> Unit,
    onEditAccept: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // 취소 버튼
        PeekrIconButton(
            modifier = Modifier.wrapContentWidth(Alignment.Start),
            icon = PeekrIcons.Default.Bold.Cancel,
            iconSize = PeekrIconSize.Normal,
            contentDescription = stringResource(R.string.keyword_detail_modal_desc_cancel),
            tint = PeekrTheme.colorScheme.textNormal,
            expandedTouchTarget = false,
            onClick = onCancel,
        )
        // 타이틀 (키워드)
        Text(
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            text = keyword,
            style = PeekrTheme.typography.headline2,
            fontWeight = FontWeight.Bold,
            color = PeekrTheme.colorScheme.textNormal,
            textAlign = TextAlign.Center,
        )
        if (myKeyword) {
            if (editMode) {
                // 수정 완료 버튼
                PeekrIconButton(
                    modifier = Modifier.wrapContentWidth(Alignment.Start),
                    icon = PeekrIcons.Default.Bold.Check,
                    iconSize = PeekrIconSize.Normal,
                    contentDescription = stringResource(R.string.keyword_detail_modal_desc_edit_accept),
                    tint = PeekrTheme.colorScheme.primary,
                    expandedTouchTarget = false,
                    onClick = onEditAccept,
                )
            } else {
                // 수정 버튼
                PeekrIconButton(
                    modifier = Modifier.wrapContentWidth(Alignment.Start),
                    icon = PeekrIcons.Outlined.Bold.Edit,
                    iconSize = PeekrIconSize.Normal,
                    contentDescription = stringResource(R.string.keyword_detail_modal_desc_edit),
                    tint = PeekrTheme.colorScheme.textNormal,
                    expandedTouchTarget = false,
                    onClick = onEdit,
                )
            }
        } else {
            // 공백
            Spacer(Modifier.size(PeekrIconSize.Normal.size))
        }
    }
}

/**
 * 다른 사용자의 키워드 내용 탭
 *
 * @param modifier [Modifier]
 * @param description 키워드 내용
 */
@Composable
private fun ColumnScope.OtherUserDescriptionTab(
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
 * 내 키워드 내용 탭
 *
 * @param modifier [Modifier]
 * @param description 키워드 내용
 * @param editMode 수정 모드 허용 여부
 * @param onDescriptionChanged 키워드 내용 변경 시
 */
@Composable
private fun ColumnScope.MyDescriptionTab(
    modifier: Modifier = Modifier,
    description: TextFieldValue,
    editMode: Boolean,
    onDescriptionChanged: (TextFieldValue) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(editMode) {
        if (editMode) {
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = description,
        onValueChange = { onDescriptionChanged(it) },
        textStyle = PeekrTheme.typography.body3Normal.copy(
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
        ),
        cursorBrush = SolidColor(PeekrTheme.colorScheme.textNormal),
        readOnly = !editMode,
    ) { innerTextField ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart,
        ) {
            innerTextField()
            if (description.text.isEmpty() && !editMode) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.keyword_detail_modal_desc_if_empty),
                    style = PeekrTheme.typography.body1,
                    fontWeight = FontWeight.Normal,
                    color = PeekrTheme.colorScheme.interactionInactive,
                    textAlign = TextAlign.Center,
                )
            }
        }
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

@Composable
private fun FullScreenError(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onCancel: () -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        PeekrIconButton(
            modifier = Modifier.align(Alignment.TopStart),
            icon = PeekrIcons.Default.Bold.Cancel,
            iconSize = PeekrIconSize.Normal,
            contentDescription = stringResource(R.string.keyword_detail_modal_desc_cancel),
            tint = PeekrTheme.colorScheme.textNormal,
            expandedTouchTarget = false,
            onClick = onCancel,
        )
        Text(
            modifier = modifier.align(Alignment.Center),
            text = errorMessage,
            style = PeekrTheme.typography.body1,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.interactionInactive,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DescriptionSkeletonScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SkeletonBox(
            Modifier
                .fillMaxWidth()
                .height(14.dp),
        )
        SkeletonBox(Modifier.size(249.dp, 14.dp))
        SkeletonBox(Modifier.size(290.dp, 14.dp))
        SkeletonBox(Modifier.size(290.dp, 14.dp))
        SkeletonBox(Modifier.size(213.dp, 14.dp))
    }
}

// ------------------------------ Preview ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun TitlePreview() {
    PeekrAppTheme {
        Title(
            modifier = Modifier.fillMaxWidth(),
            editMode = false,
            myKeyword = false,
            keyword = "키워드",
            onCancel = {},
            onEdit = {},
            onEditAccept = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun Title2Preview() {
    PeekrAppTheme {
        Title(
            modifier = Modifier.fillMaxWidth(),
            editMode = true,
            myKeyword = true,
            keyword = "키워드",
            onCancel = {},
            onEdit = {},
            onEditAccept = {},
        )
    }
}

@Preview
@Composable
private fun DescriptionSkeletonScreenPreview() {
    PeekrAppTheme {
        DescriptionSkeletonScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun KeywordDetailModalPreview() {
    var isOpen by remember { mutableStateOf(false) }
    var myKeyword by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var description by remember {
        mutableStateOf(
            TextFieldValue(
                text = "a".repeat(100),
                selection = TextRange(100),
            ),
        )
    }

    PeekrAppTheme {
        Box(Modifier.fillMaxSize()) {
            Row {
                Button(
                    onClick = {
                        myKeyword = true
                        isOpen = true
                    },
                ) { Text(text = "me") }
                Button(
                    onClick = {
                        myKeyword = false
                        isOpen = true
                    },
                ) { Text(text = "other") }
            }

            if (isOpen) {
                KeywordDetailModal(
                    modifier = Modifier,
                    sheetState = sheetState,
                    myKeyword = myKeyword,
                    keyword = "Sample Keyword",
                    description = description,
                    editMode = false,
                    loading = false,
                    loadingDescription = false,
                    fullScreenError = false,
                    fullScreenErrorMessage = "",
                    onUiEvent = {},
                    onForceCancel = {},
                )
            }
        }
    }
}
