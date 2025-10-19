package com.peekr.data.profile.repository

import com.peekr.core.data.datastore.DataStoreKey
import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.domain.coroutine.combineWithResult
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.model.Profile
import com.peekr.domain.profile.model.ProfilePatch
import com.peekr.domain.profile.model.toUserPatch
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
    private val dataStoreManager: DataStoreManager,
) : ProfileRepository {
    override fun getProfile(): Flow<Result<Profile, ErrorType>> =
        combineWithResult(
            userRepository.getUserProfile(),
            userKeywordRepository.getUserKeywords(),
        ) { user, userKeywords ->
            val profile = Profile(
                displayId = user.data.user.displayId,
                name = user.data.user.name,
                friendsTotal = user.data.friendsCount,
                profileImageUrl = user.data.user.profileImageUrl,
                introduce = user.data.user.introduce,
                keywords = userKeywords.data.keywords,
            )
            Result.Success(profile)
        }

    override fun updateProfile(patch: ProfilePatch): Flow<Result<Unit, ErrorType>> =
        userRepository.updateUser(patch.toUserPatch())

    override fun addKeyword(
        keywordName: String,
        offsetX: Double,
        offsetY: Double,
        keywordDesc: String?,
    ): Flow<Result<UserKeyword, ErrorType>> = flow {
        emit(Result.Loading)
        val userId = dataStoreManager.getLongData(DataStoreKey.User.UserId).first()
        if (userId != null) {
            val createUserKeyword = CreateUserKeyword(
                userId = UserId(userId),
                keywordName = keywordName,
                offsetX = offsetX,
                offsetY = offsetY,
                description = keywordDesc,
            )
            emitAll(userKeywordRepository.createUserKeyword(createUserKeyword))
        } else {
            // TODO: Profile 전용 에러 타입 필요
            emit(Result.Error(error = ErrorType.Unexpected(null)))
        }
    }
}
