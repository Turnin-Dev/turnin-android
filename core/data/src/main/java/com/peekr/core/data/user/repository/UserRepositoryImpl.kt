package com.peekr.core.data.user.repository

import com.peekr.core.common.IO
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.toErrorType
import com.peekr.core.data.user.network.UserDataSource
import com.peekr.core.data.user.network.request.toDataModel
import com.peekr.core.data.user.network.response.toDomainModel
import com.peekr.core.domain.coroutine.safeResultFlow
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {
    override fun getUserById(): Flow<Result<User, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            when (val result = userDataSource.getUserById()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }

    override fun updateUserById(patch: UserPatch): Flow<Result<Unit, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            when (val result = userDataSource.updateUserById(patch.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }
}
