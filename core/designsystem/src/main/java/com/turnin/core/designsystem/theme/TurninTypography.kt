package com.turnin.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.turnin.core.designsystem.R

/**
 * - `Display`: 광고 및 크게 공지할 때 사용한다.
 * - `Title`: 페이지 제목 및 큰 대목에 사용한다.
 * - `Headline`: 콘텐츠를 설명하는데 사용한다.
 * - `Body`: 본문에 사용한다.
 * - `Label`: 부가 설명에 사용한다.
 * - `Caption`: 작은 요소 안에서 사용한다.
 */
@Immutable
data class TurninTypography(
    val display1: TextStyle = TextStyle(),
    val title1: TextStyle = TextStyle(),
    val title2: TextStyle = TextStyle(),
    val headline1: TextStyle = TextStyle(),
    val headline2: TextStyle = TextStyle(),
    val headline3: TextStyle = TextStyle(),
    val headline4: TextStyle = TextStyle(),
    val headline5: TextStyle = TextStyle(),
    val body1: TextStyle = TextStyle(),
    val body2: TextStyle = TextStyle(),
    val bodyContent: TextStyle = TextStyle(),
    val body3: TextStyle = TextStyle(),
    val body4: TextStyle = TextStyle(),
    val body5: TextStyle = TextStyle(),
    val label1: TextStyle = TextStyle(),
    val label2: TextStyle = TextStyle(),
    val label3: TextStyle = TextStyle(),
    val caption1: TextStyle = TextStyle(),
    val caption2: TextStyle = TextStyle(),
    val caption3: TextStyle = TextStyle(),
)

@Composable
fun display1(): TextStyle = TextStyle(
    fontSize = 28.sp,
    lineHeight = 42.sp,
    letterSpacing = 0.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun title1(): TextStyle = TextStyle(
    fontSize = 26.sp,
    lineHeight = 39.sp,
    letterSpacing = -(0.0025).em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun title2(): TextStyle = TextStyle(
    fontSize = 24.sp,
    lineHeight = 36.sp,
    letterSpacing = 0.01.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline1(): TextStyle = TextStyle(
    fontSize = 22.sp,
    lineHeight = 33.sp,
    letterSpacing = -(0.01).em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline2(): TextStyle = TextStyle(
    fontSize = 20.sp,
    lineHeight = 30.sp,
    letterSpacing = (0.01).em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline3(): TextStyle = TextStyle(
    fontSize = 18.sp,
    lineHeight = 27.sp,
    letterSpacing = (0.01).em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline4(): TextStyle = TextStyle(
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = (0.01).em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline5(): TextStyle = TextStyle(
    fontSize = 14.sp,
    lineHeight = 21.sp,
    letterSpacing = (0.02).em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body1(): TextStyle = TextStyle(
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.005.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body2(): TextStyle = TextStyle(
    fontSize = 15.sp,
    lineHeight = 22.5.sp,
    letterSpacing = 0.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun bodyContent(): TextStyle = TextStyle(
    fontSize = 15.sp,
    lineHeight = 22.5.sp,
    letterSpacing = 0.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body3(): TextStyle = TextStyle(
    fontSize = 14.sp,
    lineHeight = 21.sp,
    letterSpacing = 0.008.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body4(): TextStyle = TextStyle(
    fontSize = 13.sp,
    lineHeight = 19.5.sp,
    letterSpacing = 0.03.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body5(): TextStyle = TextStyle(
    fontSize = 11.sp,
    lineHeight = 16.5.sp,
    letterSpacing = 0.03.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun label1(): TextStyle = TextStyle(
    fontSize = 13.sp,
    lineHeight = 19.5.sp,
    letterSpacing = 0.02.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun label2(): TextStyle = TextStyle(
    fontSize = 11.sp,
    lineHeight = 16.5.sp,
    letterSpacing = 0.02.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun label3(): TextStyle = TextStyle(
    fontSize = 10.sp,
    lineHeight = 15.sp,
    letterSpacing = (-0.01).em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun caption1(): TextStyle = TextStyle(
    fontSize = 14.sp,
    lineHeight = 21.sp,
    letterSpacing = 0.003.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun caption2(): TextStyle = TextStyle(
    fontSize = 12.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.003.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun caption3(): TextStyle = TextStyle(
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.003.em,
    fontWeight = FontWeight.Normal,
    fontFamily = pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

private val pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold, FontStyle.Normal),
)

private val lineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private val platformTextStyle = PlatformTextStyle(includeFontPadding = false)
