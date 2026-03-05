package com.peekr.core.presentation.feature.image

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberImageBitmap(uri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    return remember(uri) {
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        }
    }
}
