package com.peekr.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalColorScheme = staticCompositionLocalOf { peekrLightColor }
private val LocalTypography = staticCompositionLocalOf { PeekrTypography() }
private val LocalShape = staticCompositionLocalOf { PeekrShape() }

@Stable
object PeekrTheme {
    val colorScheme: PeekrColor
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current

    val typography: PeekrTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

@Composable
fun PeekrAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) peekrDarkColor else peekrLightColor
    val typography = PeekrTypography(
        title1 = title1(),
        title2 = title2(),
        headline1 = headline1(),
        headline2 = headline2(),
        headline3 = headline3(),
        headline4 = headline4(),
        body1 = body1(),
        body2 = body2(),
        body3Normal = body3Normal(),
        body3Many = body3Many(),
        body4 = body4(),
        label1 = label1(),
        label2 = label2(),
        caption1 = caption1(),
    )

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalTypography provides typography,
        LocalShape provides PeekrShape(),
    ) {
        content()
    }
}
