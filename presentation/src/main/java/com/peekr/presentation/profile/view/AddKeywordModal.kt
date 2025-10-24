package com.peekr.presentation.profile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrNegativeButton
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.modal.PeekrModalWrapper
import com.peekr.core.designsystem.component.textfield.PeekrSupportingText
import com.peekr.core.designsystem.component.textfield.PeekrTextField
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Arrow1Down
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.presentation.R
import com.peekr.presentation.profile.state.KeywordTextFieldState

/**
 * 키워드 추가 모달
 *
 * @param modifier [Modifier]
 * @param isOpen 모달 표시 유무
 * @param loading 로딩 표시 여부
 * @param canAdd 추가 가능 여부
 * @param keywordTextFieldState 키워드 텍스트 필드 상태
 * @param keywordDescTextFieldState 키워드 내용 텍스트 필드 상태
 * @param onKeywordTextChanged 키워드 텍스트 변화 시 콜백
 * @param onKeywordDescTextChanged 키워드 내용 텍스트 변화 시 콜백
 * @param onAddClick 추가 클릭 시 수행할 작업
 * @param onCancel 취소 클릭 시 수행할 작업
 * @param onAnimationFinished 모달이 사라지고 애니메이션까지 끝나고 나서 수행할 작업
 */
@Composable
internal fun AddKeywordModal(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    loading: Boolean,
    canAdd: Boolean,
    keywordTextFieldState: KeywordTextFieldState,
    keywordDescTextFieldState: KeywordTextFieldState,
    onKeywordTextChanged: (String) -> Unit,
    onKeywordDescTextChanged: (String) -> Unit,
    onAddClick: () -> Unit,
    onCancel: () -> Unit,
    onAnimationFinished: (() -> Unit)? = null,
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val isContentHidden by remember {
        derivedStateOf {
            val buttonHeightPx = with(density) { ButtonStyle.height.toPx() }
            scrollState.value < (scrollState.maxValue - buttonHeightPx)
        }
    }

    Box(modifier = modifier) {
        PeekrModalWrapper(
            isOpen = isOpen,
            animated = true,
            loading = loading,
            onDismissRequest = onCancel,
            onAnimationFinished = { onAnimationFinished?.invoke() },
        ) {
            Box {
                ModalContent(
                    modifier = Modifier.verticalScroll(scrollState),
                    keywordText = keywordTextFieldState.value,
                    onKeywordTextChanged = onKeywordTextChanged,
                    keywordErrorMessage = keywordTextFieldState.error?.asString(),
                    keywordDescText = keywordDescTextFieldState.value,
                    onKeywordDescTextChanged = onKeywordDescTextChanged,
                    keywordDescErrorMessage = keywordDescTextFieldState.error?.asString(),
                    canAdd = canAdd,
                    onAddClick = onAddClick,
                    onCancelClick = onCancel,
                )

                if (isContentHidden) {
                    PeekrIcon(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        icon = PeekrIcons.Default.Normal.Arrow1Down,
                        iconSize = PeekrIconSize.Small,
                        tint = PeekrTheme.colorScheme.textNormal,
                        contentDescription = stringResource(
                            R.string.profile_screen_add_keyword_modal_content_desc_more,
                        ),
                    )
                }
            }
        }
    }
}

/**
 * 모달 컨텐츠
 *
 * @param modifier [Modifier]
 * @param keywordText 키워드 텍스트
 * @param onKeywordTextChanged 키워드 텍스트 변화 시 콜백
 * @param keywordErrorMessage 키워드 에러 메시지
 * @param keywordDescText 키워드 내용 텍스트
 * @param onKeywordDescTextChanged 키워드 내용 텍스트 변화 시 콜백
 * @param keywordDescErrorMessage 키워드 내용 에러 메시지
 * @param canAdd 추가 가능 여부
 * @param onAddClick 추가 클릭 시 수행할 작업
 * @param onCancelClick 취소 클릭 시 수행할 작업
 */
@Composable
private fun ModalContent(
    modifier: Modifier = Modifier,
    keywordText: String,
    onKeywordTextChanged: (String) -> Unit,
    keywordErrorMessage: String?,
    keywordDescText: String,
    onKeywordDescTextChanged: (String) -> Unit,
    keywordDescErrorMessage: String?,
    canAdd: Boolean,
    onAddClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ModalContentGapDp),
    ) {
        // 타이틀
        Title(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.profile_screen_add_keyword_modal_title),
        )

        // 키워드 & 내용 입력 영역
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(InputSectionGapDp),
        ) {
            InputSection(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.profile_screen_add_keyword_modal_input_keyword),
                text = keywordText,
                onTextChanged = onKeywordTextChanged,
                placeholder = stringResource(R.string.profile_screen_add_keyword_modal_input_keyword_ph),
                singleLine = true,
                isError = keywordErrorMessage != null,
                errorMessage = keywordErrorMessage,
            )
            InputSection(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.profile_screen_add_keyword_modal_input_keyword_desc),
                text = keywordDescText,
                onTextChanged = onKeywordDescTextChanged,
                placeholder = stringResource(R.string.profile_screen_add_keyword_modal_input_keyword_desc_ph),
                singleLine = false,
                isError = keywordDescErrorMessage != null,
                errorMessage = keywordDescErrorMessage,
            )
        }

        // 하단 버튼 (추가 & 취소)
        Buttons(
            modifier = Modifier.fillMaxWidth(),
            canAdd = canAdd,
            onAddClick = onAddClick,
            onCancelClick = onCancelClick,
        )
    }
}

/**
 * 모달 타이틀
 *
 * @param modifier [Modifier]
 * @param text 타이틀
 */
@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        style = PeekrTheme.typography.headline2,
        fontWeight = FontWeight.Bold,
        color = PeekrTheme.colorScheme.textNormal,
        textAlign = TextAlign.Center,
    )
}

/**
 * 모달 내 입력 영역 (키워드, 키워드 내용)
 *
 * @param modifier [Modifier]
 * @param title 입력 영역 타이틀
 * @param placeholder 입력 영역 텍스트필드 자리표시자
 * @param text 입력 영역 텍스트필드 텍스트
 * @param onTextChanged 입력 영역 텍스트필드 텍스트 변화 시 콜백
 * @param singleLine 싱글 라인 여부
 * @param isError 에러 여부
 * @param errorMessage 에러 메시지
 */
@Composable
private fun InputSection(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    onTextChanged: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    isError: Boolean,
    errorMessage: String?,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = title,
            style = PeekrTheme.typography.headline3,
            fontWeight = FontWeight.Bold,
            color = PeekrTheme.colorScheme.textNormal,
        )
        PeekrTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            onTextChanged = onTextChanged,
            placeholder = placeholder,
            singleLine = singleLine,
            isError = isError,
            supportingText = {
                PeekrSupportingText(
                    text = errorMessage ?: "",
                    color = PeekrTheme.colorScheme.statusNegative,
                )
            },
        )
    }
}

/**
 * 하단 버튼 영역
 *
 * @param modifier [Modifier]
 * @param canAdd 추가 가능 여부
 * @param onAddClick `추가` 클릭 시 수행할 작업
 * @param onCancelClick `취소` 클릭 시 수행할 작업
 */
@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    canAdd: Boolean,
    onAddClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PeekrSolidButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.profile_screen_add_keyword_modal_btn_add),
            style = ButtonStyle,
            onClick = onAddClick,
            enabled = canAdd,
        )
        PeekrNegativeButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.profile_screen_add_keyword_modal_btn_cancel),
            style = ButtonStyle,
            onClick = onCancelClick,
        )
    }
}

private val InputSectionGapDp = 40.dp
private val ModalContentGapDp = 50.dp
private val ButtonStyle = PeekrButtonStyle.Medium

// ------------------------------ Previews ------------------------------
@Preview(showBackground = true)
@Composable
private fun InputKeywordPreview() {
    val (text, onTextChanged) = remember { mutableStateOf("") }
    val (text2, onText2Changed) = remember { mutableStateOf("") }

    PeekrAppTheme {
        Column {
            InputSection(
                modifier = Modifier.fillMaxWidth(),
                title = "정상 상태",
                text = text,
                onTextChanged = onTextChanged,
                placeholder = "키워드 입력",
                singleLine = true,
                isError = false,
                errorMessage = null,
            )
            InputSection(
                modifier = Modifier.fillMaxWidth(),
                title = "에러 발생 시",
                text = text2,
                onTextChanged = onText2Changed,
                placeholder = "키워드 입력",
                singleLine = true,
                isError = true,
                errorMessage = "supporting text...",
            )
        }
    }
}

@Preview
@Composable
private fun ButtonsPreview() {
    PeekrAppTheme {
        Buttons(
            modifier = Modifier.fillMaxWidth(),
            canAdd = true,
            onAddClick = {},
            onCancelClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModalContentPreview() {
    val text by remember { mutableStateOf(KeywordTextFieldState()) }
    val text2 by remember { mutableStateOf(KeywordTextFieldState()) }

    PeekrAppTheme {
        ModalContent(
            keywordText = text.value,
            onKeywordTextChanged = {},
            keywordErrorMessage = null,
            keywordDescText = text2.value,
            onKeywordDescTextChanged = {},
            keywordDescErrorMessage = null,
            canAdd = true,
            onAddClick = {},
            onCancelClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddKeywordModalPreview() {
    var isOpen by remember { mutableStateOf(false) }
    val text by remember { mutableStateOf(KeywordTextFieldState()) }
    val text2 by remember { mutableStateOf(KeywordTextFieldState()) }
    val isTextError by remember(text) {
        derivedStateOf {
            text.value.length > 5
        }
    }

    PeekrAppTheme {
        Box(Modifier.fillMaxSize()) {
            Button(onClick = { isOpen = true }) {
                Text("Modal Open")
            }

            AddKeywordModal(
                isOpen = isOpen,
                loading = false,
                canAdd = true,
                keywordTextFieldState = text,
                keywordDescTextFieldState = text2,
                onKeywordTextChanged = {},
                onKeywordDescTextChanged = {},
                onAddClick = {},
                onCancel = { isOpen = false },
            )
        }
    }
}
