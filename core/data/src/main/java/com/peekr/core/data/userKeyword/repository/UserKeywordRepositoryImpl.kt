package com.peekr.core.data.userKeyword.repository

import com.peekr.core.common.IO
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.userKeyword.network.UserKeywordDataSource
import com.peekr.core.data.userKeyword.network.request.toDataModel
import com.peekr.core.data.userKeyword.network.response.toDomainModel
import com.peekr.core.domain.coroutine.safeResultFlow
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class UserKeywordRepositoryImpl @Inject constructor(
    private val userKeywordDataSource: UserKeywordDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserKeywordRepository {
    override fun getUserKeywords(): Flow<Result<UserKeywords, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)

            when (val result = userKeywordDataSource.getUserKeywords()) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toCommonErrorType(), message = result.message))
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
                    emit(Result.Error(error = result.error.toCommonErrorType(), message = result.message))
                }
            }
        }

    override fun patchUserKeyword(
        userKeywordId: UserKeywordId,
        patch: PatchUserKeyword,
    ): Flow<Result<Unit, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)

            when (
                val result = userKeywordDataSource.patchUserKeyword(
                    userKeywordId,
                    patch.toDataModel(),
                )
            ) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toCommonErrorType(), message = result.message))
                }
            }
        }

    override fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            when (val result = userKeywordDataSource.deleteUserKeyword(userKeywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toCommonErrorType(), message = result.message))
                }
            }
        }
}
