package com.peekr.core.domain.file

import com.peekr.core.domain.file.model.Mime
import com.peekr.core.domain.file.model.PresignedUrl
import com.peekr.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** 파일 리포지토리 */
interface FileRepository {
    /**
     * 파일 업로드에 사용할 사전 정의된 URL 가져오기
     *
     * @param fileName 파일 이름
     * @param mime 파일 타입
     */
    fun getFileUploadPresignedUrl(
        fileName: String,
        mime: Mime,
    ): Flow<Result<PresignedUrl, FileErrorType>>

    /**
     * 파일 업로드
     *
     * @param presignedUrl 사전 정의된 URL
     * @param file [ByteArray]타입의 파일
     * @param fileName 파일 이름
     * @param mime 파일 타입
     *
     * @return 업로드된 파일의 URL, 파일 업로드 실패 시 `null` 반환
     */
    fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        fileName: String,
        mime: Mime,
    ): Flow<Result<String?, FileErrorType>>
}
