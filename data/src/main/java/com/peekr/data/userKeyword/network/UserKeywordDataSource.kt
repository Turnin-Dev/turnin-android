package com.peekr.data.userKeyword.network

import com.peekr.data.common.util.network.NetworkResult
import com.peekr.data.userKeyword.model.request.CreateUserKeywordRequest
import com.peekr.data.userKeyword.model.request.PatchUserKeywordRequest
import com.peekr.data.userKeyword.model.response.UserKeywordResponse
import com.peekr.data.userKeyword.model.response.UserKeywordsResponse
import com.peekr.domain.common.model.UserId
import com.peekr.domain.common.model.UserKeywordId

/** UserKeyword 네트워크 데이터 소스 */
interface UserKeywordDataSource {
    /**
     * 사용자 키워드 리스트 조회
     *
     * @param userId 사용자 ID
     */
    suspend fun getUserKeywords(userId: UserId): NetworkResult<UserKeywordsResponse>

    /**
     * 사용자 키워드 생성
     *
     * @param createUserKeywordRequest 사용자 키워드 생성 요청 바디
     */
    suspend fun createUserKeyword(
        createUserKeywordRequest: CreateUserKeywordRequest,
    ): NetworkResult<UserKeywordResponse>

    /**
     * 사용자 키워드 수정
     *
     * 반환 값은 존재하지 않으며 HTTP 상태코드로 성공 유무를 구분한다.
     * - 성공 시: `204`
     * - 실패 시: `404`
     *
     * @param ownerId 사용자 ID
     * @param userKeywordId 사용자 키워드 ID,
     * @param patchUserKeywordRequest 사용자 키워드 수정 요청 바디
     */
    suspend fun patchUserKeyword(
        ownerId: UserId,
        userKeywordId: UserKeywordId,
        patchUserKeywordRequest: PatchUserKeywordRequest,
    ): NetworkResult<Unit>

    /**
     * 사용자 키워드 삭제
     *
     * 반환 값은 존재하지 않으며 HTTP 상태코드로 성공 유무를 구분한다.
     * - 성공 시: `204`
     * - 실패 시: `404`
     *
     * @param ownerId 사용자 ID
     * @param userKeywordId 사용자 키워드 ID
     */
    suspend fun deleteUserKeyword(
        ownerId: UserId,
        userKeywordId: UserKeywordId,
    ): NetworkResult<Unit>
}
