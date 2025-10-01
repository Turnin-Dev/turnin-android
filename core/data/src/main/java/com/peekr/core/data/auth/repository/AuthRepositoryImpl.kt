package com.peekr.core.data.auth.repository

import com.peekr.core.common.IO
import com.peekr.core.data.AppConfig
import com.peekr.core.data.auth.network.AuthNetworkDataSource
import com.peekr.core.data.auth.network.request.toDataModel
import com.peekr.core.data.auth.network.response.ExistsResponse
import com.peekr.core.data.auth.network.response.toDomainModel
import com.peekr.core.data.datastore.DataStoreKey
import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.toErrorType
import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.JWTToken
import com.peekr.core.domain.auth.model.Login
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.coroutine.safeResultFlow
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.core.domain.util.toErrorCode
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl @Inject constructor(
    private val authNetworkDataSource: AuthNetworkDataSource,
    private val dataStoreManager: DataStoreManager,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {
    override fun login(login: Login): Flow<Result<JWTToken, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            when (val result = authNetworkDataSource.login(login.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }

    override fun existsUser(existsUser: ExistsUser): Flow<Result<Boolean, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            emit(mapExistsResult(authNetworkDataSource.existsUser(existsUser.toDataModel())))
        }

    override fun existsDisplayId(displayId: DisplayId): Flow<Result<Boolean, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            emit(mapExistsResult(authNetworkDataSource.existsDisplayId(displayId.toDataModel())))
        }

    override fun register(register: Register): Flow<Result<JWTToken, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            when (val result = authNetworkDataSource.register(register.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(
                        Result.Error(
                            error = result.error.toErrorType(),
                            message = result.message,
                            code = result.code?.toErrorCode(),
                        ),
                    )
                }
            }
        }

    override fun saveRefreshToken(token: String): Flow<Result<Unit, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            dataStoreManager.saveEncryptedStringData(
                key = DataStoreKey.Auth.RefreshToken,
                value = token,
            )
        }

    private fun mapExistsResult(result: NetworkResult<ExistsResponse>): Result<Boolean, ErrorType> =
        when (result) {
            is NetworkResult.Success -> Result.Success(result.data.exists)
            is NetworkResult.Error -> Result.Error(error = result.error.toErrorType(), message = result.message)
        }

    private fun createImageUrl(fileName: String): String = buildString {
        append(AppConfig.cloudStorageServerUrl.trimEnd('/'))
        append('/')
        append(fileName.trimStart('/'))
    }
}
