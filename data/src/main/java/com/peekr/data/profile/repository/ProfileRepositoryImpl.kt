package com.peekr.data.profile.repository

import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
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
    override fun updateProfile(patch: ProfilePatch): Flow<Result<Unit, ProfileErrorType>> =
        userRepository
            .updateUser(patch.toUserPatch())
            .mapError { userErrorType -> ProfileErrorType.UserError(userErrorType) }

    override fun addKeyword(
        keyword: KeywordValue,
        description: KeywordDescription,
        offsetX: Double,
        offsetY: Double,
    ): Flow<Result<UserKeyword, ProfileErrorType>> = flow {
        emit(Result.Loading)
        val userId = dataStoreManager.getLongData(DataStoreKey.User.UserId).first()
        if (userId != null) {
            val createUserKeyword = CreateUserKeyword(
                userId = UserId(userId),
                keyword = keyword,
                description = description,
                offsetX = offsetX,
                offsetY = offsetY,
            )
            emitAll(
                userKeywordRepository
                    .createUserKeyword(createUserKeyword)
                    .mapError { userKeywordErrorType ->
                        ProfileErrorType.UserKeywordError(userKeywordErrorType)
                    },
            )
        } else {
            emit(Result.Error(error = ProfileErrorType.UserNotFound))
        }
    }

    override fun deleteKeyword(userKeywordId: UserKeywordId): Flow<Result<Unit, ProfileErrorType>> =
        userKeywordRepository
            .deleteUserKeyword(userKeywordId)
            .mapError { userKeywordErrorType ->
                ProfileErrorType.UserKeywordError(userKeywordErrorType)
            }

    override fun updateOffset(
        userKeywordId: UserKeywordId,
        patchOffset: PatchOffset,
    ): Flow<Result<PatchOffset, ProfileErrorType>> =
        userKeywordRepository
            .patchOffset(userKeywordId, patchOffset)
            .mapError { userKeywordErrorType ->
                ProfileErrorType.UserKeywordError(userKeywordErrorType)
            }

    override fun updateDescription(
        userKeywordId: UserKeywordId,
        patchDescription: PatchDescription,
    ): Flow<Result<PatchDescription, ProfileErrorType>> =
        userKeywordRepository
            .patchDescription(userKeywordId, patchDescription)
            .mapError { userKeywordError ->
                ProfileErrorType.UserKeywordError(userKeywordError)
            }
}
