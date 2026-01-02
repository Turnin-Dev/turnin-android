package com.peekr.presentation.profile.view.frame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.token.ScreenTokens

/**
 * 프로필 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바 영역
 * @param profile 프로필 영역
 * @param keywords 키워드 리스트
 */
@Composable
fun ProfileScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    profile: @Composable () -> Unit,
    keywordsTitle: @Composable () -> Unit,
    keywords: @Composable () -> Unit,
) {
    Column(modifier) {
        // TopBar
        topBar()

        LazyColumn {
            // Profile
            item {
                Column(
                    Modifier
                        .padding(
                            horizontal = ScreenTokens.HorizontalPadding,
                            vertical = 10.dp,
                        )
                        .zIndex(1f),
                ) {
                    profile()
                }
            }

            // Divider
            item {
                DividerSection(Modifier.fillMaxWidth())
            }

            // Keywords Title
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PeekrTheme.colorScheme.backgroundNormal)
                        .padding(
                            horizontal = ScreenTokens.HorizontalPadding,
                            vertical = 10.dp,
                        ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    keywordsTitle()
                }
            }

            // Keywords
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenTokens.HorizontalPadding),
                ) {
                    keywords()
                }
            }
        }
    }
}

@Composable
private fun DividerSection(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 0.35.dp,
        color = PeekrTheme.colorScheme.lineDivider,
    )
}

@Preview(heightDp = 400)
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
            keywordsTitle = {
                Text(
                    text = "키워드 (0/5)",
                    style = PeekrTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                )
            },
            keywords = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .background(Color.Green),
                ) { Text("KeywordGraph") }
            },
        )
    }
}
