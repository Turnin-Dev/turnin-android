package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.turnin.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.turnin.core.data.source.network.dto.userKeyword.request.PatchUserKeywordRequest
import com.turnin.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.model.UserKeywordId

/** UserKeyword 네트워크 데이터 소스 */
interface UserKeywordNetworkDataSource {
    /**
     * 사용자 키워드 상세 정보 조회
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    suspend fun getDetail(
        userKeywordId: UserKeywordId,
    ): NetworkResult<UserKeywordDetailResponse>

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
     * @param patchUserKeywordRequest 사용자 키워드 수정 요청 바디
     */
    suspend fun patch(
        patchUserKeywordRequest: PatchUserKeywordRequest,
    ): NetworkResult<Unit>

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
