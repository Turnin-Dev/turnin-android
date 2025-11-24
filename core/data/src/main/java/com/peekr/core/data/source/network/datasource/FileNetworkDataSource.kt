package com.peekr.core.data.source.network.datasource

import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.network.api.FileApi
import com.peekr.core.data.source.network.di.DefaultOkHttpClient
import com.peekr.core.data.source.network.dto.file.response.PresignedUrlResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/** File 네트워크 데이터 소스 */
class FileNetworkDataSource @Inject constructor(
    private val fileApi: FileApi,
    @DefaultOkHttpClient private val okHttpClient: OkHttpClient,
) {
    private val tag = this::class.java.simpleName

    /**
     * 파일 업로드에 사용할 사전 정의된 URL 요청
     *
     * @param fileName 파일 이름
     * @param mime 파일 형태
     * @return 성공 시 [PresignedUrlResponse], 실패 시 [NetworkResult.Error]
     */
    suspend fun getFileUploadPresignedUrl(
        fileName: String,
        mime: String,
    ): NetworkResult<PresignedUrlResponse> =
        networkCall { fileApi.getFileUploadPresignedUrl(fileName, mime) }

    /**
     * 파일 업로드
     *
     * @param presignedUrl 사전 정의된 URL
     * @param file [ByteArray]타입의 파일
     * @param mime 파일 타입
     *
     * @return 성공 시 `true`, 실패 시 `false`
     */
    fun uploadFile(
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
