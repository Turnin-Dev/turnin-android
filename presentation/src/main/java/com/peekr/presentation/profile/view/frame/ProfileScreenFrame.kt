package com.peekr.presentation.profile.view.frame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.designsystem.util.token.ScreenTokens

/**
 * 프로필 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바 영역
 * @param profile 프로필 영역
 * @param keywordGraph 키워드 그래프 영역
 */
@Composable
fun ProfileScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    profile: @Composable () -> Unit,
    keywordGraph: @Composable () -> Unit,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        Column(
            Modifier
                .heightIn(min = TopSectionMinHeightDp)
                .zIndex(2f)
                .background(PeekrTheme.colorScheme.backgroundNormal),
        ) {
            // TopBar
            topBar()

            // Profile
            Box(
                Modifier
                    .padding(ScreenTokens.HorizontalPadding)
                    .zIndex(1f),
            ) {
                profile()
            }
        }
        ShadowSection(Modifier.fillMaxWidth())

        // Keyword Graph
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            keywordGraph()
        }
    }
}

@Composable
private fun ShadowSection(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .peekrShadow(
                type = PeekrShadowType.Custom(
                    blur = 6.dp,
                    lightColor = Color.Black,
                    darkColor = Color.White,
                    alpha = 0.25f,
                ),
            ),
        color = Color.Transparent,
    )
}

/** 프로필 정보 영역 최소 높이 (소개 글 두 줄 기준 높이) */
private val TopSectionMinHeightDp = 166.dp

@Preview
@Composable
private fun ProfileScreenFramePreview() {
    PeekrAppTheme {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.Red),
                ) { Text("TopBar") }
            },
            profile = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(166.dp)
                        .background(Color.Blue),
                ) { Text("Profile") }
            },
            keywordGraph = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Green),
                ) { Text("KeywordGraph") }
            },
        )
    }
}
