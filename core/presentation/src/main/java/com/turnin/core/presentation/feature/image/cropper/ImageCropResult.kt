package com.turnin.core.presentation.feature.image.cropper

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 잘라낸 이미지 결과
 * 1. Success: 성공 시 imageBitmap 타입의 이미지를 포함
 * 2. Failure: 실패 시 errorMessage 문자열을 포함 (nullable)
 */
internal sealed interface ImageCropResult {
    data class Success(
        val imageBitmap: ImageBitmap,
    ) : ImageCropResult

    data class Failure(
        val errorMessage: String? = null,
    ) : ImageCropResult
}
