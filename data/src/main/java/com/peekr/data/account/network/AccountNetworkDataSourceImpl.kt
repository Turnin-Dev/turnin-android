package com.peekr.data.account.network

import com.peekr.data.account.model.request.DisplayIdRequest
import com.peekr.data.account.model.request.ExistsUserRequest
import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.request.RegisterRequest
import com.peekr.data.account.model.response.ExistsResponse
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.account.model.response.PresignedUrlResponse
import com.peekr.data.account.model.response.RegisterResponse
import com.peekr.data.shared.di.DefaultOkHttpClient
import com.peekr.data.shared.util.network.NetworkErrorType
import com.peekr.data.shared.util.network.NetworkResult
import com.peekr.data.shared.util.network.networkCall
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

/** Account 네트워크 데이터 소스 */
class AccountNetworkDataSourceImpl @Inject constructor(
    private val accountApi: AccountApi,
    @DefaultOkHttpClient private val okHttpClient: OkHttpClient,
) : AccountNetworkDataSource {
    override suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse> =
        networkCall { accountApi.login(loginRequest) }

    override suspend fun existsUser(existsUserRequest: ExistsUserRequest): NetworkResult<ExistsResponse> =
        networkCall { accountApi.existsUser(existsUserRequest.provider, existsUserRequest.providerId) }

    override suspend fun existsDisplayId(displayIdRequest: DisplayIdRequest): NetworkResult<ExistsResponse> =
        networkCall { accountApi.existsDisplayId(displayIdRequest.id) }

    override suspend fun getFileUploadPresignedUrl(
        fileName: String,
        mime: String,
    ): NetworkResult<PresignedUrlResponse> =
        networkCall { accountApi.getFileUploadPresignedUrl(fileName, mime) }

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
            val result = okHttpClient.newCall(request).execute()
            NetworkResult.Success(result.isSuccessful)
        } catch (e: Exception) {
            Timber.e(e, "File upload failed: ${e.message}")
            throw e
        }

        return try {
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    NetworkResult.Success(true)
                } else {
                    Timber.w("File upload failed: HTTP ${response.code}")
                    NetworkResult.Success(false) // 정책에 따라 Error로 바꿔도 좋습니다.
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "File upload failed: ${e.message}")
            throw e
        }
    }

    override suspend fun register(registerRequest: RegisterRequest): NetworkResult<RegisterResponse> =
        networkCall { accountApi.register(registerRequest) }
}
