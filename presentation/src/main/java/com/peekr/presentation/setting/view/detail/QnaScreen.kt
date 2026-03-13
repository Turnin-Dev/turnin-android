package com.peekr.presentation.setting.view.detail

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.peekr.core.designsystem.component.button.PeekrButtonStyle
import com.peekr.core.designsystem.component.button.PeekrSolidButton
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.presentation.R

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
        PeekrTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
            onBackPressed = onBackPressed,
        )

        // 구글폼 웹뷰
        formUrl?.let {
            GoogleFormWebView(
                modifier = Modifier.weight(1f),
                formUrl = formUrl,
            )
        }
    }
}

/**
 * 구글폼 웹뷰
 *
 * @param modifier [Modifier]
 * @param formUrl 구글폼 URL
 */
@Composable
private fun GoogleFormWebView(
    modifier: Modifier = Modifier,
    formUrl: String,
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isLoading) 0f else 1f),
            factory = { context ->
                WebView(context).apply {
                    webViewRef.value = this
                    settings.javaScriptEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest,
                        ): Boolean {
                            val host = request.url.host ?: return true
                            val allowedHosts = setOf(
                                "google.com",
                                "forms.gle",
                                "docs.google.com",
                                "accounts.google.com",
                            )
                            return !allowedHosts.any { host.endsWith(it) }
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                            isError = false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }

                        override fun onReceivedError(
                            view: WebView,
                            request: WebResourceRequest,
                            error: WebResourceError,
                        ) {
                            if (request.isForMainFrame) {
                                isError = true
                                isLoading = false
                            }
                        }
                    }
                    loadUrl(formUrl)
                }
            },
            onRelease = { webView ->
                webView.stopLoading()
                webView.destroy()
            },
        )

        if (isLoading && !isError) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PeekrTheme.colorScheme.primary,
            )
        }

        if (isError) {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                onRetry = {
                    webViewRef.value?.reload()
                },
            )
        }
    }
}

/**
 * 에러 화면
 *
 * @param modifier [Modifier]
 * @param onRetry 재시도 콜백
 */
@Composable
private fun ErrorScreen(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(R.string.setting_detail_qna_page_load_failed),
            style = PeekrTheme.typography.body2,
            color = PeekrTheme.colorScheme.textNormal,
        )
        Spacer(modifier = Modifier.height(8.dp))
        PeekrSolidButton(
            text = stringResource(R.string.setting_detail_qna_page_load_failed_btn),
            style = PeekrButtonStyle.Medium,
            onClick = onRetry,
        )
    }
}

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun QnaScreenPreview() {
    PeekrAppTheme {
        QnaScreen(
            modifier = Modifier.fillMaxSize(),
            formUrl = "",
            onBackPressed = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun ErrorScreenPreview() {
    PeekrAppTheme {
        ErrorScreen(
            modifier = Modifier.fillMaxSize(),
            onRetry = {},
        )
    }
}
