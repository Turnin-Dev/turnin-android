package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.datasource.UserNetworkDataSource
import com.peekr.core.data.source.network.dto.user.request.IntroducePatchRequest
import com.peekr.core.data.source.network.dto.user.request.toDataModel
import com.peekr.core.data.source.network.dto.user.response.toDomainModel
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.user.model.MyProfile
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.model.UserProfile
import com.peekr.core.domain.user.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepositoryImpl @Inject constructor(
    private val userNetworkDataSource: UserNetworkDataSource,
    private val dataStoreManager: DataStoreManager,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {
    override suspend fun getUserId(): UserId? {
        val userId = dataStoreManager.getLongData(DataStoreKey.User.UserId).firstOrNull()
        if (userId == null) return null
        return UserId(userId)
    }

    override fun getUser(): Flow<Result<User, UserErrorType>> =
        safeResultFlow<User, UserErrorType>(ioDispatcher, { UserErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.getUser()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getMyProfile(): Flow<Result<MyProfile, UserErrorType>> =
        safeResultFlow<MyProfile, UserErrorType>(ioDispatcher, { UserErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.getMyProfile()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getUserProfile(userId: UserId): Flow<Result<UserProfile, UserErrorType>> =
        safeResultFlow<UserProfile, UserErrorType>(ioDispatcher, { UserErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.getUserProfile(userId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun updateUser(patch: UserPatch): Flow<Result<Unit, UserErrorType>> =
        safeResultFlow<Unit, UserErrorType>(ioDispatcher, { UserErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.updateUser(patch.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data))
                }

                is NetworkResult.Error -> {
                    val error = UserErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun updateIntroduce(introduce: Introduce): Flow<Result<Unit, UserErrorType>> =
        safeResultFlow<Unit, UserErrorType>(ioDispatcher, { UserErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            val introducePatchRequest = IntroducePatchRequest(introduce.value)
            when (val result = userNetworkDataSource.updateIntroduce(introducePatchRequest)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data))
                }

                is NetworkResult.Error -> {
                    val error = UserErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
