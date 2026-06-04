package com.turnin.presentation.setting.view.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.component.topbar.TurninTopBar
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.common.webview.DefaultWebView

/**
 * 문의 화면
 *
 * @param modifier [Modifier]
 * @param formUrl 구글폼 URL
 * @param onBackPressed 뒤로가기 클릭 시 콜백
 */
@Composable
fun QnaScreen(
    modifier: Modifier = Modifier,
    formUrl: String?,
    onBackPressed: () -> Unit,
) {
    Column(modifier.wrapContentHeight()) {
        // 탑바
        TurninTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
            onBackPressed = onBackPressed,
        )

        // 구글폼 웹뷰
        formUrl?.let {
            DefaultWebView(
                modifier = Modifier.weight(1f),
                url = formUrl,
            )
        }
    }
}

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun QnaScreenPreview() {
    TurninAppTheme {
        QnaScreen(
            modifier = Modifier.fillMaxSize(),
            formUrl = "",
            onBackPressed = {},
        )
    }
}
