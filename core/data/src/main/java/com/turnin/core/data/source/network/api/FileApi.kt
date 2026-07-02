package com.turnin.core.data.source.network.api

import com.turnin.core.data.source.network.dto.file.response.PresignedUrlResponse
import com.turnin.core.domain.file.model.FileCategory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/** File Network API */
interface FileApi {
    /** 파일 업로드에 사용할 PresignedURL 요청 */
    @GET(NetworkApiPath.File.UPLOAD)
    suspend fun getFileUploadPresignedUrl(
        @Query("fileName") fileName: String,
        @Query("mime") mime: String,
        @Query("fileCategory") fileCategory: FileCategory,
    ): Response<PresignedUrlResponse>

    /** 파일 업데이트에 사용할 PresignedURL 요청 */
    @GET(NetworkApiPath.File.UPDATE)
    suspend fun getFileUpdatePresignedUrl(
        @Query("newFileName") newFileName: String,
        @Query("mime") mime: String,
        @Query("fileCategory") fileCategory: FileCategory,
    ): Response<PresignedUrlResponse>
}
