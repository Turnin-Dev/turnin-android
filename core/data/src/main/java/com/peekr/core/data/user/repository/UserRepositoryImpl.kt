package com.peekr.core.data.user.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.datastore.DataStoreKey
import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.user.network.UserDataSource
import com.peekr.core.data.user.network.request.toDataModel
import com.peekr.core.data.user.network.response.toDomainModel
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.model.UserProfile
import com.peekr.core.domain.user.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val dataStoreManager: DataStoreManager,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {
    override fun getUserId(): Flow<Result<UserId, UserErrorType>> =
        safeResultFlow<UserId, UserErrorType>(ioDispatcher, { UserErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            val userId = dataStoreManager.getLongData(DataStoreKey.User.UserId).firstOrNull()
            if (userId != null) {
                emit(Result.Success(UserId(userId)))
            } else {
                emit(Result.Error(error = UserErrorType.UserIdNotFound))
            }
        }

    override fun getUser(): Flow<Result<User, UserErrorType>> =
        safeResultFlow<User, UserErrorType>(ioDispatcher, { UserErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userDataSource.getUser()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = UserErrorType.CommonError(result.error.toCommonErrorType())
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getUserProfile(): Flow<Result<UserProfile, UserErrorType>> =
        safeResultFlow<UserProfile, UserErrorType>(ioDispatcher, { UserErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userDataSource.getUserProfile()) {
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
            when (val result = userDataSource.updateUserById(patch.toDataModel())) {
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
