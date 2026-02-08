package com.peekr.core.domain.friend.repository

import androidx.paging.PagingData
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.friend.model.AddFriend
import com.peekr.core.domain.friend.model.DeleteFriend
import com.peekr.core.domain.friend.model.Friend
import com.peekr.core.domain.friend.model.FriendInfo
import com.peekr.core.domain.friend.model.IncomingRequest
import com.peekr.core.domain.friend.model.PatchFriendStatus
import com.peekr.core.domain.model.UserId
import kotlinx.coroutines.flow.Flow

/** Friend 리포지토리 */
interface FriendRepository {
    /**
     * 친구 목록 조회 (페이지네이션)
     *
     * @param userId 조회할 사용자 ID
     *
     * @return [FriendInfo] 친구 정보
     */
    fun getFriends(userId: UserId): Flow<PagingData<FriendInfo>>

    /**
     * 나에게 들어온 친구 요청 목록 조회 (페이지네이션)
     *
     * @return [IncomingRequest] 나에게 들어온 친구 요청
     */
    fun getIncomingRequests(): Flow<PagingData<IncomingRequest>>

    /**
     * 친구 추가
     *
     * 에러 별 설명
     * - [CommonErrorType.Network.Forbidden]: 요청자 ID와 실제 요청을 한 사용자 ID가 같지 않은 경우
     * - [CommonErrorType.Network.NotFound]: 사용자가 존재하지 않는 경우
     * - [CommonErrorType.Network.Conflict]: 이미 친구 요청을 했거나 친구 상태인 경우
     *
     * @param addFriend 친구 추가 모델
     * @return [Friend] 친구 모델
     */
    fun addFriend(
        addFriend: AddFriend,
    ): Flow<Result<Friend, CommonErrorType>>

    /**
     * 친구 삭제
     *
     * 에러 별 설명
     * - [CommonErrorType.Network.Forbidden]: 실제 요청을 한 사용자 ID가 요청자 ID, 요청 받을 ID와 모두 같지 않은 경우
     * - [CommonErrorType.Network.NotFound]: 친구 데이터에서 삭제 대상을 찾지 못하는 경우 (높은 확률로 이미 처리된 요청.)
     *
     * @param deleteFriend 친구 삭제 모델
     */
    fun deleteFriend(
        deleteFriend: DeleteFriend,
    ): Flow<Result<Unit, CommonErrorType>>

    /**
     * 친구 상태 수정
     *
     * 에러 별 설명
     * - [CommonErrorType.Network.Forbidden]: 요청자 ID와 실제 요청을 한 사용자 ID가 같지 않은 경우
     * - [CommonErrorType.Network.NotFound]: 친구 데이터에서 수정 대상을 찾지 못하는 경우 (높은 확률로 이미 처리된 요청.)
     *
     * @param patchFriendStatus 친구 상태 수정 모델
     */
    fun updateFriendStatus(
        patchFriendStatus: PatchFriendStatus,
    ): Flow<Result<Unit, CommonErrorType>>
}
