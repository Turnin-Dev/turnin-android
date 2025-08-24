package com.peekr.presentation.shared.image.cropper

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

/**
 * 이미지 선택기
 *
 * @param onSelected 이미지 선택시 ([ImageBitmap]타입)
 */
@Composable
fun SinglePhotoPicker(
    onSelected: (ImageBitmap?) -> Unit,
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
                }
            },
        )

    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            try {
                val imageBitmap = uriToBitmap(context, uri)
                onSelected(imageBitmap)
            } catch (e: Exception) {
                onError?.let { error ->
                    error.invoke(e)
                } ?: Toast.makeText(context, "이미지가 올바르지 않은 형식입니다.", Toast.LENGTH_SHORT).show()
                onSelected(null)
            }
        }
    }

    LaunchedEffect(Unit) {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
        )
    }
}
