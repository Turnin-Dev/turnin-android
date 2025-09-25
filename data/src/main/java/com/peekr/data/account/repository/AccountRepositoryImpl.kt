package com.peekr.data.account.repository

import com.peekr.core.common.IO
import com.peekr.core.data.datastore.DataStoreKey
import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.toErrorType
import com.peekr.core.domain.coroutine.safeResultFlow
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.core.domain.util.toErrorCode
import com.peekr.data.AppConfig
import com.peekr.data.account.model.request.toDataModel
import com.peekr.data.account.model.response.ExistsResponse
import com.peekr.data.account.model.response.toDomainModel
import com.peekr.data.account.network.AccountNetworkDataSource
import com.peekr.domain.account.model.ExistsUser
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.Mime
import com.peekr.domain.account.model.PresignedUrl
import com.peekr.domain.account.model.Register
import com.peekr.domain.account.repository.AccountRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class AccountRepositoryImpl @Inject constructor(
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val dataStoreManager: DataStoreManager,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : AccountRepository {
    override fun login(login: Login): Flow<Result<JWTToken, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            when (val result = accountNetworkDataSource.login(login.toDataModel())) {
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
            emit(mapExistsResult(accountNetworkDataSource.existsUser(existsUser.toDataModel())))
        }

    override fun existsDisplayId(displayId: DisplayId): Flow<Result<Boolean, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            emit(mapExistsResult(accountNetworkDataSource.existsDisplayId(displayId.toDataModel())))
        }

    override fun getFileUploadPresignedUrl(fileName: String, mime: Mime): Flow<Result<PresignedUrl, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            when (val result = accountNetworkDataSource.getFileUploadPresignedUrl(fileName, mime.type)) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }

    override fun uploadFile(
        presignedUrl: String,
        file: ByteArray,
        fileName: String,
        mime: Mime,
    ): Flow<Result<String?, ErrorType>> = safeResultFlow(ioDispatcher) {
        emit(Result.Loading)
        when (val result = accountNetworkDataSource.uploadFile(presignedUrl, file, mime.type)) {
            is NetworkResult.Success -> {
                val imageUrl = createImageUrl(fileName)
                if (result.data) {
                    emit(Result.Success(imageUrl))
                } else {
                    emit(Result.Success(null))
                }
            }

            is NetworkResult.Error -> {
                emit(Result.Error(error = result.error.toErrorType(), message = result.message))
            }
        }
    }

    override fun register(register: Register): Flow<Result<JWTToken, ErrorType>> =
        safeResultFlow(ioDispatcher) {
            emit(Result.Loading)
            when (val result = accountNetworkDataSource.register(register.toDataModel())) {
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
