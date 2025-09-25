package com.peekr.data.userKeyword.repository

import com.peekr.core.common.IO
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.toErrorType
import com.peekr.core.domain.coroutine.safeResultFlow
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.data.userKeyword.model.request.toDataModel
import com.peekr.data.userKeyword.model.response.toDomainModel
import com.peekr.data.userKeyword.network.UserKeywordDataSource
import com.peekr.domain.userKeyword.model.CreateUserKeyword
import com.peekr.domain.userKeyword.model.PatchUserKeyword
import com.peekr.domain.userKeyword.model.UserKeyword
import com.peekr.domain.userKeyword.model.UserKeywords
import com.peekr.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class UserKeywordRepositoryImpl @Inject constructor(
    private val userKeywordDataSource: UserKeywordDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserKeywordRepository {
    override fun getUserKeywords(userId: UserId): Flow<Result<UserKeywords, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)

            when (val result = userKeywordDataSource.getUserKeywords(userId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }

    override fun createUserKeyword(create: CreateUserKeyword): Flow<Result<UserKeyword, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)

            when (val result = userKeywordDataSource.createUserKeyword(create.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }

    override fun patchUserKeyword(
        userId: UserId,
        userKeywordId: UserKeywordId,
        patch: PatchUserKeyword,
    ): Flow<Result<Unit, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)

            when (
                val result = userKeywordDataSource.patchUserKeyword(
                    userId,
                    userKeywordId,
                    patch.toDataModel(),
                )
            ) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }

    override fun deleteUserKeyword(
        userId: UserId,
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            when (val result = userKeywordDataSource.deleteUserKeyword(userId, userKeywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }
}
