package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.file.response.PresignedUrlResponse
import com.turnin.core.data.source.network.util.NetworkResult

/** File 네트워크 데이터 소스 */

interface FileNetworkDataSource {
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
     * 파일 업데이트에 사용할 사전 정의된 URL 요청
     *
     * @param newFileName 새로운 파일 이름
     * @param mime 파일 형태
     * @return 성공 시 [PresignedUrlResponse], 실패 시 [NetworkResult.Error]
     */
    suspend fun getFileUpdatePresignedUrl(
        newFileName: String,
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
    fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        mime: String,
    ): NetworkResult<Boolean>
}
