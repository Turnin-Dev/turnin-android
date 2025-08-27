package com.peekr.data.account.network

import com.peekr.data.account.model.request.DisplayIdRequest
import com.peekr.data.account.model.request.ExistsUserRequest
import com.peekr.data.account.model.request.LoginRequest
import com.peekr.data.account.model.request.RegisterRequest
import com.peekr.data.account.model.response.ExistsResponse
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.account.model.response.PresignedUrlResponse
import com.peekr.data.account.model.response.RegisterResponse
import com.peekr.data.shared.util.network.NetworkResult

/** Account 네트워크 데이터 소스 */
interface AccountNetworkDataSource {
    /**
     * 로그인
     *
     * @param loginRequest 로그인 요청 바디
     * @return 성공 시 - [NetworkResult.Success]
     * @return 실패 시 - [NetworkResult.Error]
     */
    suspend fun login(loginRequest: LoginRequest): NetworkResult<LoginResponse>

    /**
     * 사용자 존재 여부 확인
     *
     * @param existsUserRequest 사용자 존재 여부 확인 요청 바디
     * @return [ExistsResponse] - 존재하면 `ExistsResponse(true)`, 존재하지 않으면 `ExistsResponse(false)`
     */
    suspend fun existsUser(existsUserRequest: ExistsUserRequest): NetworkResult<ExistsResponse>

    /**
     * 사용자 표시 ID 존재 여부 확인
     *
     * @param DisplayIdRequest 요청용 사용자 표시 ID
     * @return [ExistsResponse] - 존재하면 `ExistsResponse(true)`, 존재하지 않으면 `ExistsResponse(false)`
     */
    suspend fun existsDisplayId(displayId: DisplayIdRequest): NetworkResult<ExistsResponse>

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
    ): NetworkResult<PresignedUrlResponse>

    /**
     * 파일 업로드
     *
     * @param presignedUrl 사전 정의된 URL
     * @param file [ByteArray]타입의 파일
     * @param mime 파일 타입
     *
     * @return 성공 시 `true`, 실패 시 `false`
     */
    suspend fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        mime: String,
    ): NetworkResult<Boolean>

    /**
     * 회원가입
     *
     * @param registerRequest 회원가입 요청 바디
     */
    suspend fun register(registerRequest: RegisterRequest): NetworkResult<RegisterResponse>
}
