package com.turnin.core.presentation.common.webview

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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.turnin.core.designsystem.component.button.TurninButtonStyle
import com.turnin.core.designsystem.component.button.TurninSolidButton
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.presentation.R

/**
 * 기본 웹뷰
 *
 * @param modifier [Modifier]
 * @param url 웹뷰 URL
 * @param javaScriptEnabled 자바스크립트 허용 여부
 * @param allowedHosts 허용 호스트 목록
 */
@Composable
fun DefaultWebView(
    modifier: Modifier = Modifier,
    url: String,
    javaScriptEnabled: Boolean = true,
    allowedHosts: Set<String> = setOf(
        "google.com",
        "forms.gle",
        "docs.google.com",
        "accounts.google.com",
    ),
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
                    settings.javaScriptEnabled = javaScriptEnabled
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest,
                        ): Boolean {
                            val host = request.url.host ?: return true
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
                    loadUrl(url)
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
                color = TurninTheme.colorScheme.primary,
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
            .background(TurninTheme.colorScheme.backgroundNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(R.string.default_webview_load_failed),
            style = TurninTheme.typography.body2,
            color = TurninTheme.colorScheme.textNormal,
        )
        Spacer(modifier = Modifier.height(8.dp))
        TurninSolidButton(
            text = stringResource(R.string.default_webview_load_failed_btn),
            style = TurninButtonStyle.Medium,
            onClick = onRetry,
        )
    }
}

// ------------------------------ Previews ------------------------------

@PreviewLightDark
@Composable
private fun DefaultWebViewPreview() {
    TurninAppTheme {
        DefaultWebView(
            modifier = Modifier.fillMaxSize(),
            url = "",
        )
    }
}

@PreviewLightDark
@Composable
private fun ErrorScreenPreview() {
    TurninAppTheme {
        ErrorScreen(
            modifier = Modifier.fillMaxSize(),
            onRetry = {},
        )
    }
}
