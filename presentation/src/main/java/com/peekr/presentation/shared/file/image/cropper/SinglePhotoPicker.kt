package com.peekr.presentation.shared.file.image.cropper

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
import com.peekr.presentation.R

/**
 * 이미지 선택기
 *
 * @param open 이미지 선택기 활성화 여부
 * @param onSelected 이미지 선택시 ([ImageBitmap]타입)
 * @param onClose 이미지 선택시 닫을 시 수행할 작업 (Ex. open = false)
 * @param onError 에러 발생 시 수행할 작업
 */
@Composable
fun SinglePhotoPicker(
    open: Boolean,
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
            }
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
