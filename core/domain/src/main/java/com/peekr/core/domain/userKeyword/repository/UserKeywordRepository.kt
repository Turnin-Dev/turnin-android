package com.peekr.core.domain.userKeyword.repository

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import kotlinx.coroutines.flow.Flow

/** 사용자 키워드 리포지토리 */
interface UserKeywordRepository {
    /**
     * 나의 키워드 상세 정보를 로컬 DB에서 조회한다.
     *
     * @param userKeywordId 사용자 키워드 ID
     *
     * @return 데이터가 존재하면 [UserKeywordDetail]를 반환하고,
     * 없으면 `null`을 반환하므로 별도의 조회가 필요하다.
     */
    fun getMyDetailFromLocal(
        userKeywordId: UserKeywordId,
    ): Flow<UserKeywordDetail?>

    /**
     * 사용자 키워드 상세 정보 조회
     *
     * @param userId 사용자 ID
     * @param userKeywordId 사용자 키워드 ID
     */
    fun getDetail(
        userId: UserId,
        userKeywordId: UserKeywordId,
    ): Flow<Result<UserKeywordDetail, CommonErrorType>>

    /**
     * 사용자 키워드 상세 정보 조회 새로고침 (무조건 네트워크에서 조회)
     *
     * @param userId 사용자 ID
     * @param userKeywordId 사용자 키워드 ID
     */
    fun getDetailRefresh(
        userId: UserId,
        userKeywordId: UserKeywordId,
    ): Flow<Result<UserKeywordDetail, CommonErrorType>>

    /**
     * 나의 키워드 리스트를 로컬에서 조회한다.
     */
    fun getMyKeywords(): Flow<List<UserKeyword>>

    /**
     * 나의 키워드 상세 정보 리스트를 조회해서 로컬 데이터에 업데이트한다.
     */
    fun getMyKeywordsRefresh(): Flow<Result<Unit, CommonErrorType>>

    /**
     * 사용자의 키워드 상세 정보 리스트 조회
     *
     * @param userId 사용자 ID
     */
    fun getUserKeywords(userId: UserId): Flow<Result<List<UserKeywordDetail>, CommonErrorType>>

    /**
     * 사용자 키워드 설명 조회
     */
    @Deprecated("삭제 예정 - 설명만 별도로 조회하는 기능은 필요 없을 것으로 예상된다.")
    fun getDescription(
        userKeywordId: UserKeywordId,
    ): Flow<Result<KeywordDescription, CommonErrorType>>

    /**
     * 사용자 키워드 생성
     *
     * @param create 사용자 키워드 생성 요청 객체
     */
    fun createUserKeyword(create: CreateUserKeyword): Flow<Result<UserKeyword, CommonErrorType>>

    /**
     * 사용자 키워드 설명 수정
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param patchDescription 사용자 키워드 설명 수정 요청 객체
     */
    fun patchDescription(
        userKeywordId: UserKeywordId,
        patchDescription: PatchDescription,
    ): Flow<Result<PatchDescription, CommonErrorType>>

    /**
     * 사용자 키워드 삭제
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, CommonErrorType>>
}
