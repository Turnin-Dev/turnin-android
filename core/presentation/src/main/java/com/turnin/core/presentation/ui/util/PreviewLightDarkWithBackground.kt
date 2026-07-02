package com.turnin.core.presentation.ui.util

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

/**
 * PreviewLightDark를 확장하여 배경이 활성화된 버전
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    backgroundColor = 0xFF000000,
)
annotation class PreviewLightDarkWithBackground

@Preview(showBackground = true)
annotation class PreviewWithBackground
