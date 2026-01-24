package com.peekr.presentation.keywordEdit.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Check
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.modifier.accessibility
import com.peekr.presentation.R
import com.peekr.presentation.keywordEdit.state.KeywordEditContract
import com.peekr.presentation.profile.state.KeywordTextFieldState
import kotlinx.coroutines.launch

/**
 * 키워드 편집 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바
 * @param inputKeyword 키워드 입력 영역
 * @param inputDescription 키워드 내용 입력 영역
 */
@Composable
private fun KeywordEditScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable ColumnScope.() -> Unit,
    inputKeyword: @Composable ColumnScope.() -> Unit,
    inputDescription: @Composable BoxScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var inputDescHeight by remember { mutableIntStateOf(0) }

    Column(
        modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        topBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = ScreenTokens.HorizontalPadding, vertical = 10.dp)
                .height(IntrinsicSize.Min),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            inputKeyword()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        val diff = it.height - inputDescHeight
                        inputDescHeight = it.height
                        if (diff == 0 || inputDescHeight == 0) {
                            return@onSizeChanged
                        }

                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.value + diff)
                        }
                    },
            ) {
                inputDescription()
            }
        }
    }
}

/**
 * 키워드 편집 화면
 *
 * @param modifier [Modifier]
 * @param uiState UI 상태
 * @param onUiEvent UI 이벤트
 */
@Composable
fun KeywordEditScreen(
    modifier: Modifier = Modifier,
    uiState: KeywordEditContract.UiState,
    onUiEvent: (KeywordEditContract.UiEvent) -> Unit,
) {
    Box(modifier) {
        KeywordEditScreenFrame(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            topBar = {
                TopBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                    onAddClick = {
                        onUiEvent(KeywordEditContract.UiEvent.AddOrUpdateKeyword)
                    },
                    onBackPressed = {
                        onUiEvent(KeywordEditContract.UiEvent.SafeBackPressed)
                    },
                )
            },
            inputKeyword = {
                InputKeyword(
                    modifier = Modifier.fillMaxWidth(),
                    keywordTextFieldState = uiState.keyword,
                    onKeywordChanged = {
                        onUiEvent(KeywordEditContract.UiEvent.OnKeywordChanged(it))
                    },
                )
            },
            inputDescription = {
                InputDescription(
                    modifier = Modifier.fillMaxSize(),
                    description = uiState.description,
                    onDescriptionChanged = {
                        onUiEvent(KeywordEditContract.UiEvent.OnDescriptionChanged(it))
                    },
                )
            },
        )

        if (uiState.loading) {
            PeekrLoadingScreen()
        }
    }
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param onAddClick 추가 클릭 시
 * @param onBackPressed 뒤로가기 클릭 시
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onBackPressed: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        onBackPressed = onBackPressed,
        optionSlot = {
            PeekrIconButton(
                icon = PeekrIcons.Default.Normal.Check,
                iconSize = PeekrIconSize.Small,
                contentDescription = stringResource(R.string.keyword_edit_screen_add),
                tint = PeekrTheme.colorScheme.primary,
                onClick = onAddClick,
            )
        },
    )
}

/**
 * 키워드 입력 영역
 *
 * @param modifier [Modifier]
 * @param keywordTextFieldState 키워드 텍스트필드 상태
 * @param onKeywordChanged 키워드 텍스트 변화 시 콜백
 */
@Composable
private fun InputKeyword(
    modifier: Modifier = Modifier,
    keywordTextFieldState: KeywordTextFieldState,
    onKeywordChanged: (String) -> Unit,
) {
    val isError = keywordTextFieldState.error != null && keywordTextFieldState.value.isNotEmpty()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        KeywordTextField(
            modifier = Modifier.fillMaxWidth(),
            text = keywordTextFieldState.value,
            onTextChanged = { onKeywordChanged(it) },
            placeholder = stringResource(R.string.keyword_edit_screen_input_keyword_placeholder),
            isError = isError,
        )
        if (isError) {
            ErrorMessage(
                errorMessage = keywordTextFieldState.error.asString(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 키워드 내용 입력 영역
 *
 * @param modifier [Modifier]
 * @param description 키워드 내용
 * @param onDescriptionChanged 키워드 내용 변경 시 콜백
 */
@Composable
private fun InputDescription(
    modifier: Modifier = Modifier,
    description: String,
    onDescriptionChanged: (String) -> Unit,
) {
    DescriptionTextField(
        modifier = modifier,
        text = description,
        onTextChanged = { onDescriptionChanged(it) },
        placeholder = stringResource(R.string.keyword_edit_screen_input_description_placeholder),
    )
}

@Composable
private fun ErrorMessage(
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.accessibility(errorMessage),
        text = errorMessage,
        style = PeekrTheme.typography.caption1,
        color = PeekrTheme.colorScheme.statusNegative,
        textAlign = TextAlign.Start,
    )
}

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun TopBarPreview() {
    PeekrAppTheme {
        TopBar(
            modifier = Modifier.fillMaxWidth(),
            onAddClick = {},
            onBackPressed = {},
        )
    }
}

@Preview
@Composable
private fun KeywordEditScreenPreview() {
    PeekrAppTheme {
        KeywordEditScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = KeywordEditContract.UiState(),
            onUiEvent = {},
        )
    }
}
