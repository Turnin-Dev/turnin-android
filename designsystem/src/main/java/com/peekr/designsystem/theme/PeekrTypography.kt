package com.peekr.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Immutable
data class PeekrTypography(
    val title1: TextStyle = TextStyle(),
    val title2: TextStyle = TextStyle(),
    val headline1: TextStyle = TextStyle(),
    val headline2: TextStyle = TextStyle(),
    val headline3: TextStyle = TextStyle(),
    val headline4: TextStyle = TextStyle(),
    val body1: TextStyle = TextStyle(),
    val body2: TextStyle = TextStyle(),
    val body3Normal: TextStyle = TextStyle(),
    val body3Many: TextStyle = TextStyle(),
    val body4: TextStyle = TextStyle(),
    val label1: TextStyle = TextStyle(),
    val label2: TextStyle = TextStyle(),
    val caption1: TextStyle = TextStyle(),
)

@Composable
fun title1(): TextStyle = TextStyle(
    fontSize = 26.sp,
    lineHeight = 39.sp,
    letterSpacing = -(0.0025).em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun title2(): TextStyle = TextStyle(
    fontSize = 24.sp,
    lineHeight = 36.sp,
    letterSpacing = 0.01.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline1(): TextStyle = TextStyle(
    fontSize = 20.sp,
    lineHeight = 30.sp,
    letterSpacing = -(0.025).em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline2(): TextStyle = TextStyle(
    fontSize = 18.sp,
    lineHeight = 27.sp,
    letterSpacing = -(0.001).em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline3(): TextStyle = TextStyle(
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun headline4(): TextStyle = TextStyle(
    fontSize = 14.sp,
    lineHeight = 21.sp,
    letterSpacing = 0.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body1(): TextStyle = TextStyle(
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.005.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body2(): TextStyle = TextStyle(
    fontSize = 15.sp,
    lineHeight = 21.75.sp,
    letterSpacing = 0.01.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body3Normal(): TextStyle = TextStyle(
    fontSize = 14.sp,
    lineHeight = 20.3.sp,
    letterSpacing = 0.01.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body3Many(): TextStyle = TextStyle(
    fontSize = 14.sp,
    lineHeight = 20.3.sp,
    letterSpacing = -(0.025).em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun body4(): TextStyle = TextStyle(
    fontSize = 13.sp,
    lineHeight = 18.2.sp,
    letterSpacing = 0.01.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun label1(): TextStyle = TextStyle(
    fontSize = 13.sp,
    lineHeight = 18.2.sp,
    letterSpacing = 0.02.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun label2(): TextStyle = TextStyle(
    fontSize = 11.sp,
    lineHeight = 15.4.sp,
    letterSpacing = 0.03.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)

@Composable
fun caption1(): TextStyle = TextStyle(
    fontSize = 12.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.003.em,
    fontWeight = FontWeight.Normal,
    fontFamily = Pretendard,
    lineHeightStyle = lineHeightStyle,
    platformStyle = platformTextStyle,
)
