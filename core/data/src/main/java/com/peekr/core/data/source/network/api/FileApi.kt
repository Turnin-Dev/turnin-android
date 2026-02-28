package com.peekr.core.data.source.network.api

import com.peekr.core.data.source.network.api.NetworkApiPath
import com.peekr.core.data.source.network.dto.file.response.PresignedUrlResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/** File Network API */
interface FileApi {
    /** 파일 업로드에 사용할 사전 정의된 URL 요청 */
    @GET(NetworkApiPath.File.UPLOAD)
    suspend fun getFileUploadPresignedUrl(
        @Query("fileName") fileName: String,
        @Query("mime") mime: String,
    ): Response<PresignedUrlResponse>

    /** 파일 업데이트에 사용할 사전 정의된 URL 요청 */
    @GET(NetworkApiPath.File.UPDATE)
    suspend fun getFileUpdatePresignedUrl(
        @Query("newFileName") newFileName: String,
        @Query("mime") mime: String,
    ): Response<PresignedUrlResponse>
}
