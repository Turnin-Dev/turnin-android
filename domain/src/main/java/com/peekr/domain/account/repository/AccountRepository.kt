package com.peekr.domain.account.repository

import com.peekr.domain.account.model.DisplayId
import com.peekr.domain.account.model.ExistsUser
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.Mime
import com.peekr.domain.account.model.PresignedUrl
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import kotlinx.coroutines.flow.Flow

/** 계정 관련 리포지토리 */
interface AccountRepository {
    /** 로그인 */
    fun login(login: Login): Flow<Result<JWTToken, ErrorType>>

    /** 사용자 존재 여부 확인 */
    fun existsUser(existsUser: ExistsUser): Flow<Result<Boolean, ErrorType>>

    /** 사용자 표시 ID 존재 여부 확인 */
    fun existsDisplayId(displayId: DisplayId): Flow<Result<Boolean, ErrorType>>

    /**
     * 파일 업로드에 사용할 사전 정의된 URL 가져오기
     *
     * @param fileName 파일 이름
     * @param mime 파일 타입
     */
    fun getFileUploadPresignedUrl(
        fileName: String,
        mime: Mime,
    ): Flow<Result<PresignedUrl, ErrorType>>

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
    ): Flow<Result<String?, ErrorType>>
}
