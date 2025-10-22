package com.peekr.core.data.file.network

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.file.network.response.PresignedUrlResponse
import com.peekr.core.data.network.DefaultOkHttpClient
import com.peekr.core.data.network.error.NetworkErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.networkCall
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class FileNetworkDataSource @Inject constructor(
    private val fileApi: FileApi,
    @DefaultOkHttpClient private val okHttpClient: OkHttpClient,
) : FileDataSource {
    private val tag = this::class.java.simpleName

    override suspend fun getFileUploadPresignedUrl(
        fileName: String,
        mime: String,
    ): NetworkResult<PresignedUrlResponse> =
        networkCall { fileApi.getFileUploadPresignedUrl(fileName, mime) }

    override suspend fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        mime: String,
    ): NetworkResult<Boolean> {
        val mediaType = mime.toMediaTypeOrNull()
        if (mediaType == null) {
            return NetworkResult.Error(NetworkErrorType.Network.InvalidFileType)
        }

        val requestBody = file.toRequestBody(mediaType)
        val request = Request
            .Builder()
            .url(presignedUrl)
            .put(requestBody)
            .build()

        return try {
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    NetworkResult.Success(true)
                } else {
                    AppLogger.w(tag, "File upload failed: HTTP ${response.code}")
                    NetworkResult.Error(NetworkErrorType.Network.UploadFileFailed)
                }
            }
        } catch (e: Exception) {
            AppLogger.e(tag, e, "File upload failed")
            NetworkResult.Error(NetworkErrorType.Network.UploadFileFailed)
        }
    }
}
