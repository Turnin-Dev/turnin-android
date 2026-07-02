package com.turnin.core.presentation.feature.image

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.turnin.core.presentation.feature.image.cropper.uriToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberImageBitmap(uri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    var imageBitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        imageBitmap = uri?.let {
            withContext(Dispatchers.IO) {
                uriToBitmap(context, it)
            }
        }
    }

    return imageBitmap
}
