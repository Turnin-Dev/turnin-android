package com.peekr.presentation.register.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Cancel
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.presentation.image.cropper.ImageCropper
import com.peekr.core.presentation.token.ScreenTokens
import com.peekr.presentation.R

/**
 * 회원가입 - 프로필 사진 자르기 화면
 *
 * @param modifier [Modifier]
 * @param image [ImageBitmap]타입의 자를 원본 이미지
 * @param onCancel 취소 시
 * @param onCrop 이미지를 자르고 자른 이미지를 제공
 */
@Composable
fun CropProfileImageScreen(
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
        PeekrIconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(ScreenTokens.HorizontalPaddingWithTouchTarget),
            icon = PeekrIcons.Default.Normal.Cancel,
            iconSize = PeekrIconSize.Small,
            contentDescription = stringResource(R.string.common_btn_cancel),
            tint = PeekrTheme.colorScheme.staticWhite,
            onClick = onCancel,
        )
    }
}

@Preview
@Composable
private fun CropProfileImageScreenPreview() {
    PeekrAppTheme {
        CropProfileImageScreen(
            modifier = Modifier.fillMaxSize(),
            image = null,
            onCancel = {},
            onCrop = { imageBitmap ->
            },
        )
    }
}
