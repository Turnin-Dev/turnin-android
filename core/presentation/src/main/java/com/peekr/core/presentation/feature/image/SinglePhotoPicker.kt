package com.peekr.core.presentation.feature.image

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.peekr.core.presentation.R
import com.peekr.core.presentation.feature.image.cropper.uriToBitmap
import kotlin.math.ceil

/**
 * 이미지 선택기
 *
 * @param open 이미지 선택기 활성화 여부
 * @param maxFileSizeBytes 이미지 최대 크기
 * @param onSelected 이미지 선택시 ([ImageBitmap]타입)
 * @param onClose 이미지 선택시 닫을 시 수행할 작업 (Ex. open = false)
 * @param onError 에러 발생 시 수행할 작업
 */
@Composable
fun SinglePhotoPicker(
    open: Boolean,
    maxFileSizeBytes: Long = MAX_FILE_SIZE_BYTES,
    onSelected: (ImageBitmap?) -> Unit,
    onClose: () -> Unit,
    onError: ((Exception) -> Unit)? = null,
) {
    val context = LocalContext.current
    var selectedImageUri: Uri? by rememberSaveable { mutableStateOf(null) }

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    selectedImageUri = uri
                } else {
                    onClose()
                }
            },
        )

    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            try {
                // 파일 크기 확인 및 에러 처리
                val fileSize = context.contentResolver.openFileDescriptor(uri, "r")
                    ?.use { it.statSize } ?: -1L
                if (fileSize > maxFileSizeBytes || fileSize < 0) {
                    val maxFileSizeMbForMessage =
                        ceil(maxFileSizeBytes / (1024.0 * 1024.0)).toInt()
                    Toast
                        .makeText(
                            context,
                            context.getString(
                                R.string.single_photo_picker_invalid_max_size_exceed,
                                maxFileSizeMbForMessage,
                            ),
                            Toast.LENGTH_SHORT,
                        ).show()
                    onSelected(null)
                    selectedImageUri = null
                    return@let
                }

                // 파일 변환 및 선택 수행
                val imageBitmap = uriToBitmap(context, uri)
                onSelected(imageBitmap)
                selectedImageUri = null
            } catch (e: Exception) {
                onError?.invoke(e) ?: Toast
                    .makeText(
                        context,
                        context.getText(R.string.single_photo_picker_invalid_image_format),
                        Toast.LENGTH_SHORT,
                    ).show()
                onSelected(null)
            } finally {
                onClose()
            }
        }
    }

    LaunchedEffect(open) {
        if (open) {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        }
    }
}

private const val MAX_FILE_SIZE_BYTES: Long = 10 * 1024 * 1024
