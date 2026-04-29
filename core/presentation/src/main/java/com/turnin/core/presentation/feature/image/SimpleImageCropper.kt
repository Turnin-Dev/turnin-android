package com.turnin.core.presentation.feature.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.turnin.core.designsystem.component.button.TurninIconButton
import com.turnin.core.designsystem.component.icon.TurninIconSize
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.icon.Cancel
import com.turnin.core.designsystem.util.icon.TurninIcons
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.R
import com.turnin.core.presentation.feature.image.cropper.ImageCropper

/**
 * 기본 사진 편집기
 *
 * @param modifier [Modifier]
 * @param image [ImageBitmap]타입의 자를 원본 이미지
 * @param onCancel 취소 시
 * @param onCrop 이미지를 자르고 자른 이미지를 제공
 */
@Composable
fun SimpleImageCropper(
    modifier: Modifier = Modifier,
    image: ImageBitmap?,
    onCancel: () -> Unit,
    onCrop: (ImageBitmap) -> Unit,
) {
    Box(modifier) {
        // 이미지 자르기 화면
        if (image != null) {
            ImageCropper(
                imageBitmap = image,
                onCrop = { croppedImageBitmap ->
                    onCrop(croppedImageBitmap)
                },
            )
        }
        // 취소 버튼
        TurninIconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(ScreenTokens.HorizontalPaddingWithTouchTarget),
            icon = TurninIcons.Default.Normal.Cancel,
            iconSize = TurninIconSize.Small,
            contentDescription = stringResource(R.string.simple_image_cropper_cancel),
            tint = TurninTheme.colorScheme.staticWhite,
            onClick = onCancel,
        )
    }
}

@Preview
@Composable
private fun SimpleImageCropperPreview() {
    TurninAppTheme {
        SimpleImageCropper(
            modifier = Modifier.fillMaxSize(),
            image = null,
            onCancel = {},
            onCrop = { imageBitmap ->
            },
        )
    }
}
