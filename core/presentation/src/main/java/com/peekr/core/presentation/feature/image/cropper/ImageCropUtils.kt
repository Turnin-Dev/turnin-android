package com.peekr.core.presentation.feature.image.cropper

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.lang.Exception
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 이미지 자르기 함수
 *
 * 주의사항: 사용 시 (시작 오프셋 + (너비 or 높이)) 가 이미지의 크기를 초과하면 안된다.
 *
 * @param density dp 에서 px 로 변환을 위해 사용
 * @param imageBitmap ImageBitmap 타입의 이미지 (nullable)
 * @param scale 이미지의 Scale 값
 * @param viewWidth 화면 사이즈의 가로 길이
 * @param viewHeight 화면 사이즈의 세로 길이
 * @param offsetChanged 자르고 싶은 부분의 Offset 값(기준은 TopLeft)
 *
 * @return CropImageResult
 */
internal fun cropImage(
    density: Density,
    imageBitmap: ImageBitmap?,
    scale: Float = 1f,
    viewWidth: Int,
    viewHeight: Int,
    offsetChanged: Offset,
): ImageCropResult =
    if (imageBitmap == null) {
        ImageCropResult.Failure("다른 사진으로 시도 해 주세요.")
    } else if (viewWidth <= 0 || viewHeight <= 0 || scale <= 0f || !scale.isFinite()) {
        ImageCropResult.Failure("이미지 자르기 값이 올바르지 않습니다.")
    } else {
        val imageWidth = imageBitmap.width
        val imageHeight = imageBitmap.height
        val widthRatio = imageWidth / viewWidth.toFloat()
        val heightRatio = imageHeight / viewHeight.toFloat()

        val cropRatio = max(widthRatio, heightRatio)
        val radiusPx = with(density) { HOLE_RADIUS.dp.toPx() } * cropRatio

        val centerX = imageWidth / 2f
        val centerY = imageHeight / 2f

        // scale이 클수록 즉, 확대 시 src 영역은 작아져야 함
        val srcRadius = radiusPx / scale
        val srcSize = (srcRadius * 2).toInt().coerceAtLeast(1)

        // offset을 이미지 픽셀 좌표로 변환할 때도 cropRatio 사용
        val srcOffsetX = (centerX - srcRadius) + (-offsetChanged.x * cropRatio)
        val srcOffsetY = (centerY - srcRadius) + (-offsetChanged.y * cropRatio)

        // dst는 항상 홀 지름 고정
        val dstSize = (radiusPx * 2).toInt().coerceAtLeast(1)

        // 이미지 범위 밖으로 나간 경우 클램핑
        val clampedSrcOffsetX = srcOffsetX.toInt().coerceIn(0, (imageWidth - srcSize).coerceAtLeast(0))
        val clampedSrcOffsetY = srcOffsetY.toInt().coerceIn(0, (imageHeight - srcSize).coerceAtLeast(0))

        // 클램핑 후 실제 잘라낼 수 있는 크기 재계산
        val actualSrcWidth = srcSize.coerceAtMost(imageWidth - clampedSrcOffsetX)
        val actualSrcHeight = srcSize.coerceAtMost(imageHeight - clampedSrcOffsetY)

        val croppedImage = ImageBitmap(dstSize, dstSize)
        val canvas = Canvas(croppedImage)

        canvas.drawImageRect(
            image = imageBitmap,
            srcOffset = IntOffset(clampedSrcOffsetX, clampedSrcOffsetY),
            srcSize = IntSize(actualSrcWidth, actualSrcHeight),
            dstOffset = IntOffset(0, 0),
            dstSize = IntSize(dstSize, dstSize),
            paint = Paint(),
        )
        ImageCropResult.Success(croppedImage)
    }

/**
 * Uri 를 Bitmap(ImageBitmap)으로 변환 시켜준다.
 *
 * 시간이 걸리는 작업일 수 있으므로 코루틴 사용 권장
 *
 * ```
 * /** 사용 예시 */
 *     LaunchedEffect(selectedImageUri) {
 *         selectedImageUri?.let { image ->
 *             val imageBitmapDeferred =
 *                 coroutineScope.async(Dispatchers.IO) {
 *                     uriToBitmap(context, image)
 *                 }
 *             imageBitmap = imageBitmapDeferred.await()
 *         }
 *     }
 * ```
 *
 * @param imageUri 이미지의 Uri
 *
 * @return 성공 시 [ImageBitmap], 실패 시 `null`을 반환한다.
 */
suspend fun uriToBitmap(
    context: Context,
    imageUri: Uri,
): ImageBitmap? = withContext(Dispatchers.IO) {
    try {
        val loader = Coil.imageLoader(context)
        val request = ImageRequest
            .Builder(context)
            .data(imageUri)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            val drawable = result.drawable
            if (drawable is BitmapDrawable) {
                return@withContext drawable.bitmap.asImageBitmap()
            }
        }
        null
    } catch (e: Exception) {
        null
    }
}
