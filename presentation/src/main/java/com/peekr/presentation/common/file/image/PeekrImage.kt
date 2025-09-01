package com.peekr.presentation.common.file.image

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

private val FORMAT = Bitmap.CompressFormat.JPEG
private const val QUALITY = 100

fun ImageBitmap.toByteArray(): ByteArray {
    val androidBitmap = this.asAndroidBitmap()
    val byteArrayOutputStream = ByteArrayOutputStream()
    androidBitmap.compress(FORMAT, QUALITY, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

// class PeekrImage {
//    private var _imageBitmap: ImageBitmap? = null
//    private var _byteArray: ByteArray? = null
//    private val format = Bitmap.CompressFormat.JPEG
//    private val quality = 100
//
//    val imageBitmap: ImageBitmap
//        get() {
//            if (_imageBitmap == null && _byteArray != null) {
//                val bitmap = BitmapFactory.decodeByteArray(_byteArray!!, 0, _byteArray!!.size)
//                _imageBitmap = bitmap.asImageBitmap()
//            }
//            return _imageBitmap!!
//        }
//
//    val byteArray: ByteArray
//        get() {
//            if (_byteArray == null && _imageBitmap != null) {
//                val androidBitmap = _imageBitmap!!.asAndroidBitmap()
//                val byteArrayOutputStream = ByteArrayOutputStream()
//                androidBitmap.compress(format, quality, byteArrayOutputStream)
//                _byteArray = byteArrayOutputStream.toByteArray()
//            }
//            return _byteArray!!
//        }
//
//    constructor(imageBitmap: ImageBitmap) {
//        _imageBitmap = imageBitmap
//    }
//
//    constructor(byteArray: ByteArray) {
//        _byteArray = byteArray
//    }
// }
