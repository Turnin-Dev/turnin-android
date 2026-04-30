package com.turnin.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalColorScheme = staticCompositionLocalOf { turninLightColor }
private val LocalTypography = staticCompositionLocalOf { TurninTypography() }
private val LocalShape = staticCompositionLocalOf { TurninShape() }
private val LocalTransition = staticCompositionLocalOf { TurninTransition() }

@Stable
object TurninTheme {
    val colorScheme: TurninColor
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current

    val typography: TurninTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val shape: TurninShape
        @Composable
        @ReadOnlyComposable
        get() = LocalShape.current

    val transition: TurninTransition
        @Composable
        @ReadOnlyComposable
        get() = LocalTransition.current
}

@Composable
fun TurninAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) turninDarkColor else turninLightColor
    val typography = TurninTypography(
        display1 = display1(),
        title1 = title1(),
        title2 = title2(),
        headline1 = headline1(),
        headline2 = headline2(),
        headline3 = headline3(),
        headline4 = headline4(),
        headline5 = headline5(),
        body1 = body1(),
        body2 = body2(),
        body3 = body3(),
        bodyContent = bodyContent(),
        body4 = body4(),
        body5 = body5(),
        label1 = label1(),
        label2 = label2(),
        label3 = label3(),
        caption1 = caption1(),
        caption2 = caption2(),
        caption3 = caption3(),
    )

    @OptIn(ExperimentalMaterial3Api::class)
    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalTypography provides typography,
        LocalShape provides TurninShape(),
        LocalRippleConfiguration provides TurninRipple,
        LocalTransition provides TurninTransition(),
    ) {
        content()
    }
}
