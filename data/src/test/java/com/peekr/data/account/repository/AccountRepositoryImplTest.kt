package com.peekr.data.account.repository

import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.account.network.AccountNetworkDataSource
import com.peekr.data.shared.util.NetworkResult
import com.peekr.data.shared.util.network.NetworkErrorType
import com.peekr.data.shared.util.network.toErrorType
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.model.UserUID
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountRepositoryImplTest {
    private val dataSource: AccountNetworkDataSource = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: AccountRepository = AccountRepositoryImpl(dataSource, dispatcher)

    @Test
    fun `login() 성공 테스트`() = runTest {
        // given
        coEvery { dataSource.login(any()) } returns NetworkResult.Success(mockLoginResponse)

        // when
        val result = repository.login(mockLogin).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals((result as Result.Success).data, mockJWTToken)
    }

    @Test
    fun `login() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() = runTest {
        // given
        coEvery {
            dataSource.login(any())
        } returns NetworkResult.Error(error = NetworkErrorType.Network.Conflict, message = mockErrorMessage)

        // when
        val result = repository.login(mockLogin).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toErrorType())
        assertEquals(result.message, mockErrorMessage)
    }

    companion object {
        private val mockLogin = Login(SocialLoginProvider.GOOGLE, UserUID("123"))
        private val mockAccessToken = "aaa.bbb.ccc"
        private val mockRefreshToken = "rrr.bbb.ccc"
        private val mockLoginResponse = LoginResponse(mockAccessToken, mockRefreshToken)
        private val mockJWTToken = JWTToken(mockAccessToken, mockRefreshToken)
        private val mockErrorMessage = "error world!"
    }
}
