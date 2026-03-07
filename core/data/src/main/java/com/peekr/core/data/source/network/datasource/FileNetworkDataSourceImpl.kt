package com.peekr.core.data.source.network.datasource

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.di.DefaultOkHttpClient
import com.peekr.core.data.source.network.api.FileApi
import com.peekr.core.data.source.network.dto.file.response.PresignedUrlResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class FileNetworkDataSourceImpl @Inject constructor(
    private val fileApi: FileApi,
    @DefaultOkHttpClient private val okHttpClient: OkHttpClient,
) : FileNetworkDataSource {
    private val tag = this::class.java.simpleName

    override suspend fun getFileUploadPresignedUrl(
        fileName: String,
        mime: String,
    ): NetworkResult<PresignedUrlResponse> =
        networkCall { fileApi.getFileUploadPresignedUrl(fileName, mime) }

    override suspend fun getFileUpdatePresignedUrl(
        newFileName: String,
        mime: String,
    ): NetworkResult<PresignedUrlResponse> =
        networkCall { fileApi.getFileUpdatePresignedUrl(newFileName, mime) }

    override fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        mime: String,
    ): NetworkResult<Boolean> {
        val mediaType = mime.toMediaTypeOrNull()
        if (mediaType == null) {
            return NetworkResult.Error(NetworkErrorType.Network.InvalidFileType)
        }

        val requestBody = file.toRequestBody(mediaType)
        // 서버에서 Presigned URL 생성 시 사용한 스펙과 통일되어야 한다.
        val request = Request
            .Builder()
            .url(presignedUrl)
            .addHeader("Content-Type", mime)
            .addHeader("Cache-Control", "public, max-age=31536000, immutable")
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
