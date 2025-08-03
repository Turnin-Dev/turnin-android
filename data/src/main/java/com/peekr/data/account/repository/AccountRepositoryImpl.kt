package com.peekr.data.account.repository

import com.peekr.data.account.model.request.toDataModel
import com.peekr.data.account.network.AccountNetworkDataSource
import com.peekr.data.shared.di.IO
import com.peekr.data.shared.util.NetworkResult
import com.peekr.data.shared.util.coroutine.safeResultFlow
import com.peekr.data.shared.util.network.toErrorType
import com.peekr.domain.account.model.ExistsUser
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class AccountRepositoryImpl @Inject constructor(
    private val accountNetworkDataSource: AccountNetworkDataSource,
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
            when (val result = accountNetworkDataSource.existsUser(existsUser.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.isExists))
                }

                is NetworkResult.Error -> {
                    emit(Result.Error(error = result.error.toErrorType(), message = result.message))
                }
            }
        }
}
