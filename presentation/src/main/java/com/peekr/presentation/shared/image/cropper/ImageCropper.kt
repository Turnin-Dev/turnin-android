package com.peekr.presentation.shared.image.cropper

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.component.button.PeekrButtonStyle
import com.peekr.designsystem.component.button.PeekrSolidButton
import com.peekr.presentation.R
import com.peekr.presentation.shared.util.ScreenTokens

/**
 * 이미지 자르기 화면
 *
 * @param modifier [Modifier]
 * @param imageBitmap [ImageBitmap]타입의 이미지
 * @param onCrop 확인 버튼 클릭 시 이미지를 자르고 자른 이미지를 제공
 *
 * @see ImageCropResult
 * @see cropImage
 * @see uriToBitmap
 */
@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap?,
    onCrop: (ImageBitmap) -> Unit,
) {
    val density = LocalDensity.current

    var viewWidth by remember { mutableIntStateOf(0) }
    var viewHeight by remember { mutableIntStateOf(0) }

    var scale by remember { mutableFloatStateOf(1.5f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val imageTransformState =
        rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            val maxX = (viewWidth.toFloat() * (scale - 1f) / 2f) / scale
            val maxY = (viewHeight.toFloat() * (scale - 1f) / 2f) / scale
            scale = (scale * zoomChange).coerceIn(1f, 3f)
            offset =
                Offset(
                    x = (offset.x + offsetChange.x).coerceIn(-maxX, maxX),
                    y = (offset.y + offsetChange.y).coerceIn(-maxY, maxY),
                )
        }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged {
                viewWidth = it.width
                viewHeight = it.height
            },
    ) {
        HoleFrameView {
            imageBitmap?.let { imageBitmap ->
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier =
                        Modifier
                            .align(Alignment.Center)
//                        .clipToBounds()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x * scale,
                                translationY = offset.y * scale,
                            ).transformable(imageTransformState),
                )
            }
        }

        PeekrSolidButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(ScreenTokens.HorizontalPadding),
            text = stringResource(id = R.string.register_screen_btn_image_crop),
            style = PeekrButtonStyle.Large,
            onClick = {
                imageBitmap?.let { imageBitmap ->
                    val cropImageResult =
                        cropImage(
                            density = density,
                            imageBitmap = imageBitmap,
                            scale = scale,
                            viewWidth = viewWidth,
                            viewHeight = viewHeight,
                            offsetChanged = offset,
                        )
                    when (cropImageResult) {
                        is ImageCropResult.Success -> {
                            onCrop(cropImageResult.imageBitmap)
                        }

                        is ImageCropResult.Failure -> {
                            /** 에러 처리 **/
                        }
                    }
                }
            },
        )
    }
}

/**
 * 이미지를 자를 부분을 선택하는 원 모양의 구멍이 뚫린 프레임 뷰
 */
@Composable
private fun HoleFrameView(
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithContent {
                /** px **/
                val width = size.width

                /** px **/
                val height = size.height

                drawContent()

                drawWithLayer {
                    drawRect(Color(0xFF212121).copy(0.8f))

                    drawCircle(
                        color = Color.Transparent,
//                        radius = (width / 2) - padding,
                        radius = HOLE_RADIUS.dp.toPx(),
                        center = Offset(width / 2, height / 2),
                        blendMode = BlendMode.SrcIn,
                    )
                }
            },
    ) {
        image()
    }
}

/**
 * BlendMode 와 함께 레이어를 그려 겹치게 해준다.
 */
private fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

/** px: 480.9375 (171dp 에서 변환된 값) */
const val HOLE_RADIUS = 171
