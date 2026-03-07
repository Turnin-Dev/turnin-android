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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 이미지 선택기
 *
 * @param open 이미지 선택기 활성화 여부
 * @param maxFileSizeBytes 이미지 최대 크기
 * @param onSelected 이미지 선택 시 콜백 ([ImageBitmap], [Uri])
 * @param onClose 이미지 선택시 닫을 시 수행할 작업 (Ex. open = false)
 * @param onError 에러 발생 시 수행할 작업
 */
@Composable
fun SinglePhotoPicker(
    open: Boolean,
    maxFileSizeBytes: Long = MAX_FILE_SIZE_BYTES,
    onSelected: (imageBitmap: ImageBitmap?, uri: Uri?) -> Unit,
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
        val uri = selectedImageUri ?: return@LaunchedEffect

        try {
            // 파일 크기 확인
            val fileSize = withContext(Dispatchers.IO) {
                context.contentResolver.openFileDescriptor(uri, "r")
                    ?.use { it.statSize } ?: -1L
            }

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
                onSelected(null, null)
                return@LaunchedEffect
            }

            // 파일 변환(URI -> ImageBitmap)
            val imageBitmap = withContext(Dispatchers.IO) {
                uriToBitmap(context, uri)
            }

            // 파일 변환 실패 시
            if (imageBitmap == null) {
                onSelected(null, null)
                return@LaunchedEffect
            }

            // 파일 변환 성공 시
            onSelected(imageBitmap, uri)
        } catch (e: Exception) {
            onError?.invoke(e) ?: Toast
                .makeText(
                    context,
                    context.getText(R.string.single_photo_picker_invalid_image_format),
                    Toast.LENGTH_SHORT,
                ).show()
            onSelected(null, null)
        } finally {
            selectedImageUri = null
            onClose()
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
