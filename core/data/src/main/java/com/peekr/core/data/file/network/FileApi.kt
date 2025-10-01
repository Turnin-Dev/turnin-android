package com.peekr.core.data.file.network

import com.peekr.core.data.file.network.response.PresignedUrlResponse
import com.peekr.core.data.network.NetworkApiPath
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
}
