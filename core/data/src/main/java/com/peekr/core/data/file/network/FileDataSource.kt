package com.peekr.core.data.file.network

import com.peekr.core.data.file.network.response.PresignedUrlResponse
import com.peekr.core.data.network.util.NetworkResult

/** 파일 네트워크 데이터소스 */
interface FileDataSource {
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
}
