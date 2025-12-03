package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchDescriptionRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchOffsetRequest
import com.peekr.core.data.source.network.dto.userKeyword.response.DescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchDescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchOffsetResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordsResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId

/** UserKeyword 네트워크 데이터 소스 */
interface UserKeywordNetworkDataSource {
    /**
     * 사용자 키워드 리스트 조회
     */
    suspend fun getUserKeywords(
        userId: UserId,
    ): NetworkResult<UserKeywordsResponse>

    /**
     * 사용자 키워드 설명 조회
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    suspend fun getDescription(
        userKeywordId: UserKeywordId,
    ): NetworkResult<DescriptionResponse>

    /**
     * 사용자 키워드 생성
     *
     * @param createUserKeywordRequest 사용자 키워드 생성 요청 바디
     */
    suspend fun createUserKeyword(
        createUserKeywordRequest: CreateUserKeywordRequest,
    ): NetworkResult<UserKeywordResponse>

    /**
     * 사용자 키워드 오프셋 수정
     *
     * @param userKeywordId 사용자 키워드 ID,
     * @param patchOffsetRequest 사용자 키워드 오프셋 수정 요청 바디
     */
    suspend fun patchOffset(
        userKeywordId: UserKeywordId,
        patchOffsetRequest: PatchOffsetRequest,
    ): NetworkResult<PatchOffsetResponse>

    /**
     * 사용자 키워드 설명 수정
     *
     * @param userKeywordId 사용자 키워드 ID,
     * @param patchDescriptionRequest 사용자 키워드 설명 수정 요청 바디
     */
    suspend fun patchDescription(
        userKeywordId: UserKeywordId,
        patchDescriptionRequest: PatchDescriptionRequest,
    ): NetworkResult<PatchDescriptionResponse>

    /**
     * 사용자 키워드 삭제
     *
     * 반환 값은 존재하지 않으며 HTTP 상태코드로 성공 유무를 구분한다.
     * - 성공 시: `204`
     * - 실패 시: `404`
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    suspend fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): NetworkResult<Unit>
}
