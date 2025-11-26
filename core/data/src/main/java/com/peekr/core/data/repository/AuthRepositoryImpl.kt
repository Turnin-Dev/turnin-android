package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.datasource.AuthNetworkDataSource
import com.peekr.core.data.source.network.dto.auth.request.toDataModel
import com.peekr.core.data.source.network.dto.auth.response.ExistsResponse
import com.peekr.core.data.source.network.dto.auth.response.toDomainModel
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.model.RegisterResult
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.model.DisplayId
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl @Inject constructor(
    private val authNetworkDataSource: AuthNetworkDataSource,
    private val dataStoreManager: DataStoreManager,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {
    override fun login(loginCredentials: LoginCredentials): Flow<Result<LoginResult, CommonErrorType>> =
        safeResultFlow<LoginResult, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = authNetworkDataSource.login(loginCredentials.toDataModel())) {
                is NetworkResult.Success -> {
                    dataStoreManager.saveLongData(
                        key = DataStoreKey.User.UserId,
                        value = result.data.userId,
                    )
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(
                        Result.Error(
                            error = result.error.toCommonErrorType(),
                            message = result.message,
                        ),
                    )
                }
            }
        }

    override fun existsUser(existsUser: ExistsUser): Flow<Result<Boolean, CommonErrorType>> =
        safeResultFlow<Boolean, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            emit(mapExistsResult(authNetworkDataSource.existsUser(existsUser.toDataModel())))
        }

    override fun existsDisplayId(displayId: DisplayId): Flow<Result<Boolean, CommonErrorType>> =
        safeResultFlow<Boolean, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            emit(mapExistsResult(authNetworkDataSource.existsDisplayId(displayId)))
        }

    override fun register(register: Register): Flow<Result<RegisterResult, CommonErrorType>> =
        safeResultFlow<RegisterResult, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (val result = authNetworkDataSource.register(register.toDataModel())) {
                is NetworkResult.Success -> {
                    dataStoreManager.saveLongData(
                        key = DataStoreKey.User.UserId,
                        value = result.data.userId,
                    )
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(
                        Result.Error(
                            error = result.error.toCommonErrorType(),
                            message = result.message,
                        ),
                    )
                }
            }
        }

    override fun saveRefreshToken(token: String): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            dataStoreManager.saveEncryptedStringData(
                key = DataStoreKey.Auth.RefreshToken,
                value = token,
            )
        }

    private fun mapExistsResult(result: NetworkResult<ExistsResponse>): Result<Boolean, CommonErrorType> =
        when (result) {
            is NetworkResult.Success -> Result.Success(result.data.exists)
            is NetworkResult.Error -> Result.Error(
                error = result.error.toCommonErrorType(),
                message = result.message,
            )
        }
}
