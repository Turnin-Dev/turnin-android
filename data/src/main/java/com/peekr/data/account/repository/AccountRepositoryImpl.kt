package com.peekr.data.account.repository

import com.peekr.data.account.model.request.toDataModel
import com.peekr.data.account.network.AccountNetworkDataSource
import com.peekr.data.shared.di.IO
import com.peekr.data.shared.util.NetworkResult
import com.peekr.data.shared.util.coroutine.safeResultFlow
import com.peekr.data.shared.util.network.toErrorType
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
    /**
         * 로그인 요청을 수행하고 JWT 토큰 또는 오류 정보를 Flow로 반환합니다.
         *
         * @param login 로그인에 필요한 도메인 모델 정보.
         * @return 로그인 진행 상태 및 결과(JWT 토큰 또는 오류 타입)를 순차적으로 방출하는 Flow.
         */
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
}
