package com.turnin.presentation.register.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.button.TurninButtonStyle
import com.turnin.core.designsystem.component.button.TurninSolidButton
import com.turnin.core.designsystem.component.radio.TurninRadioButton
import com.turnin.core.designsystem.component.topbar.TurninTopBar
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.designsystem.util.click.clickableSingleWithoutRipple
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.presentation.R

/**
 * 약관 동의 화면
 *
 * @param modifier [Modifier]
 * @param onNavigateToNext 다음 화면으로 이동
 */
@Composable
fun TermsAgreementScreen(
    modifier: Modifier = Modifier,
    onNavigateToNext: () -> Unit,
) {
    var serviceAgreed by remember { mutableStateOf(false) }
    var privacyAgreed by remember { mutableStateOf(false) }
    val allAgreed by remember { derivedStateOf { serviceAgreed && privacyAgreed } }

    RegisterScreenFrame(
        modifier = modifier,
        topBar = {
            TurninTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
            )
        },
        contents = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    RegisterCommonScreenToken.ContentVerticalSpacing,
                ),
            ) {
                Text(
                    text = stringResource(R.string.register_screen_terms_title),
                    style = TurninTheme.typography.title1,
                    fontWeight = FontWeight.Bold,
                    color = TurninTheme.colorScheme.textNormal,
                )

                TermsAgreementItems(
                    allAgreed = allAgreed,
                    serviceAgreed = serviceAgreed,
                    privacyAgreed = privacyAgreed,
                    onAllAgreeClick = {
                        serviceAgreed = !allAgreed
                        privacyAgreed = !allAgreed
                    },
                    onServiceAgreeClick = { serviceAgreed = !serviceAgreed },
                    onPrivacyAgreeClick = { privacyAgreed = !privacyAgreed },
                    onViewServicePolicy = { /* TODO */ },
                    onViewPrivacyPolicy = { /* TODO */ },
                )
            }
        },
        bottomButton = {
            TurninSolidButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        bottom = ScreenTokens.BottomButtonPadding,
                        top = ScreenTokens.BottomButtonPadding,
                    ),
                text = stringResource(R.string.register_screen_btn_next),
                style = TurninButtonStyle.Large,
                onClick = onNavigateToNext,
                enabled = allAgreed,
                loading = false,
            )
        },
    )
}

/**
 * 약관 동의 항목 리스트
 *
 * @param allAgreed 전체 동의 여부
 * @param serviceAgreed 서비스 이용약관 동의 여부
 * @param privacyAgreed 개인정보 처리방침 동의 여부
 * @param onAllAgreeClick 전체 동의 클릭 콜백
 * @param onServiceAgreeClick 서비스 이용약관 클릭 콜백
 * @param onPrivacyAgreeClick 개인정보 처리방침 클릭 콜백
 * @param onViewServicePolicy 서비스 이용약관 보기 클릭 콜백
 * @param onViewPrivacyPolicy 개인정보 처리방침 보기 클릭 콜백
 */
@Composable
private fun TermsAgreementItems(
    allAgreed: Boolean,
    serviceAgreed: Boolean,
    privacyAgreed: Boolean,
    onAllAgreeClick: () -> Unit,
    onServiceAgreeClick: () -> Unit,
    onPrivacyAgreeClick: () -> Unit,
    onViewServicePolicy: () -> Unit,
    onViewPrivacyPolicy: () -> Unit,
) {
    Column {
        TermsAgreementItem(
            text = stringResource(R.string.register_screen_terms_all_agree),
            selected = allAgreed,
            onClick = onAllAgreeClick,
            isAllAgree = true,
        )

        Spacer(Modifier.height(22.dp))

        TermsAgreementItem(
            text = stringResource(R.string.register_screen_terms_service),
            selected = serviceAgreed,
            onClick = onServiceAgreeClick,
            onViewClick = onViewServicePolicy,
        )

        Spacer(Modifier.height(14.dp))

        TermsAgreementItem(
            text = stringResource(R.string.register_screen_terms_privacy_policy),
            selected = privacyAgreed,
            onClick = onPrivacyAgreeClick,
            onViewClick = onViewPrivacyPolicy,
        )
    }
}

/**
 * 약관 동의 항목
 *
 * @param modifier [Modifier]
 * @param text 항목 내용
 * @param selected 선택 여부
 * @param isAllAgree 전체 동의 여부
 * @param onClick 클릭 콜백
 * @param onViewClick 보기 클릭 콜백
 */
@Composable
private fun TermsAgreementItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    isAllAgree: Boolean = false,
    onClick: () -> Unit,
    onViewClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSingleWithoutRipple(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TurninRadioButton(
                modifier = Modifier.size(24.dp),
                selected = selected,
                onClick = onClick,
            )
            Text(
                text = text,
                style = if (!isAllAgree) TurninTheme.typography.body3 else TurninTheme.typography.body1,
                fontWeight = if (!isAllAgree) FontWeight.Normal else FontWeight.Medium,
                color = TurninTheme.colorScheme.textNormal,
            )
        }

        onViewClick?.let {
            Text(
                modifier = Modifier.clickableSingle(onClick = it),
                text = stringResource(R.string.register_screen_terms_view),
                style = TurninTheme.typography.body3,
                fontWeight = FontWeight.Normal,
                color = TurninTheme.colorScheme.textAssist,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsAgreementScreenPreview() {
    TurninAppTheme {
        TermsAgreementScreen(
            onNavigateToNext = {},
        )
    }
}
