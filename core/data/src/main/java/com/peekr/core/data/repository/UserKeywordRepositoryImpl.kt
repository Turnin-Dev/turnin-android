package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.local.database.dao.MyKeywordDetailDao
import com.peekr.core.data.source.local.database.entity.toDomainModel
import com.peekr.core.data.source.local.database.entity.toEntity
import com.peekr.core.data.source.network.datasource.UserKeywordNetworkDataSource
import com.peekr.core.data.source.network.datasource.UserNetworkDataSource
import com.peekr.core.data.source.network.dto.common.toDomainModel
import com.peekr.core.data.source.network.dto.userKeyword.request.toDataModel
import com.peekr.core.data.source.network.dto.userKeyword.response.toDomainModel
import com.peekr.core.data.source.network.dto.userKeyword.response.toEntity
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class UserKeywordRepositoryImpl @Inject constructor(
    private val userKeywordNetworkDataSource: UserKeywordNetworkDataSource,
    private val userNetworkDataSource: UserNetworkDataSource,
    private val myKeywordDetailDao: MyKeywordDetailDao,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : UserKeywordRepository {
    private val tag = this::class.java.simpleName

    override fun getDetail(
        userKeywordId: UserKeywordId,
        withUserInfo: Boolean,
    ): Flow<Result<UserKeywordDetail, CommonErrorType>> =
        safeResultFlow<UserKeywordDetail, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.getDetail(userKeywordId, withUserInfo)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getMyKeywords(): Flow<List<UserKeywordDetail>> =
        myKeywordDetailDao.getAll()
            .map { it.map { it.toDomainModel() } }
            .flowOn(ioDispatcher)

    override fun getMyKeywordsRefresh(): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.getMyKeywords()) {
                is NetworkResult.Success -> {
                    AppLogger.d(tag, "My keywords refresh successful")
                    val myKeywords = result.data.map { it.toDomainModel() }
                    myKeywordDetailDao.upsertAll(myKeywords.map { it.toEntity() })
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    AppLogger.d(tag, "My keywords refresh failure")
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getUserKeywords(userId: UserId): Flow<Result<List<UserKeywordDetail>, CommonErrorType>> =
        safeResultFlow<List<UserKeywordDetail>, CommonErrorType>(
            ioDispatcher,
            { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)
            when (val result = userNetworkDataSource.getUserKeywords(userId.value)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.map { it.toDomainModel() }))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun getDescription(
        userKeywordId: UserKeywordId,
    ): Flow<Result<KeywordDescription, CommonErrorType>> =
        safeResultFlow<KeywordDescription, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.getDescription(userKeywordId)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun createUserKeyword(create: CreateUserKeyword): Flow<Result<UserKeyword, CommonErrorType>> =
        safeResultFlow<UserKeyword, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.createUserKeyword(create.toDataModel())) {
                is NetworkResult.Success -> {
                    val entity = result.data.toEntity()
                    myKeywordDetailDao.upsert(entity)
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun patchDescription(
        userKeywordId: UserKeywordId,
        patchDescription: PatchDescription,
    ): Flow<Result<PatchDescription, CommonErrorType>> =
        safeResultFlow<PatchDescription, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (
                val result = userKeywordNetworkDataSource.patchDescription(
                    userKeywordId,
                    patchDescription.toDataModel(),
                )
            ) {
                is NetworkResult.Success -> {
                    myKeywordDetailDao.updateDescription(
                        userKeywordId = userKeywordId.value,
                        description = patchDescription.description.value,
                    )
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun deleteUserKeyword(
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(
            dispatcher = ioDispatcher,
            unexpectedErrorMapper = { CommonErrorType.Unexpected(it) },
        ) {
            emit(Result.Loading)

            when (val result = userKeywordNetworkDataSource.deleteUserKeyword(userKeywordId)) {
                is NetworkResult.Success -> {
                    myKeywordDetailDao.deleteById(userKeywordId.value)
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
