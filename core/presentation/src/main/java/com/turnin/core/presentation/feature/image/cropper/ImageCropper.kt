package com.turnin.core.presentation.feature.image.cropper

import android.widget.Toast
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
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.button.TurninButtonStyle
import com.turnin.core.designsystem.component.button.TurninSolidButton
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.R

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
internal fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap?,
    onCrop: (ImageBitmap) -> Unit,
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val defaultErrorMsg = stringResource(R.string.image_cropper_error_default)

    var viewWidth by remember { mutableIntStateOf(0) }
    var viewHeight by remember { mutableIntStateOf(0) }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val imageTransformState =
        rememberTransformableState { zoomChange, offsetChange, _ ->
            val newScale = (scale * zoomChange).coerceIn(1f, 3f)

            val imageWidth = imageBitmap?.width?.toFloat() ?: viewWidth.toFloat()
            val imageHeight = imageBitmap?.height?.toFloat() ?: viewHeight.toFloat()

            val fitScale = minOf(viewWidth / imageWidth, viewHeight / imageHeight)

            val renderedWidth = imageWidth * fitScale
            val renderedHeight = imageHeight * fitScale

            val maxX = (renderedWidth * (newScale - 1f) / 2f) / newScale
            val maxY = (renderedHeight * (newScale - 1f) / 2f) / newScale

            scale = newScale
            offset = Offset(
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
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .clipToBounds()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x * scale,
                                translationY = offset.y * scale,
                            )
                            .transformable(imageTransformState),
                )
            }
        }

        TurninSolidButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(ScreenTokens.HorizontalPadding),
            text = stringResource(id = R.string.image_cropper_btn_image_crop),
            style = TurninButtonStyle.Large,
            enabled = imageBitmap != null && viewWidth > 0 && viewHeight > 0,
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
                            val errorMsg = cropImageResult.errorMessage ?: defaultErrorMsg
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
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
