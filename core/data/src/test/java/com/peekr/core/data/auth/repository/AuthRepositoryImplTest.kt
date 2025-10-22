package com.peekr.core.data.auth.repository

import com.peekr.core.data.auth.network.AuthDataSource
import com.peekr.core.data.auth.network.response.ExistsResponse
import com.peekr.core.data.auth.network.response.LoginResponse
import com.peekr.core.data.auth.network.response.RegisterResponse
import com.peekr.core.data.auth.network.response.toDomainModel
import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.data.network.error.NetworkErrorType
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.Login
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.util.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {
    private val dataSource: AuthDataSource = mockk()
    private val dataStoreManager: DataStoreManager = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: AuthRepository =
        AuthRepositoryImpl(dataSource, dataStoreManager, dispatcher)

    @Test
    fun `login() 성공 테스트`() =
        runTest {
            // given
            coEvery { dataSource.login(any()) } returns NetworkResult.Success(mockLoginResponse)
            coEvery { dataStoreManager.saveLongData(any(), any()) } just Runs

            // when
            val result = repository.login(mockLogin).last()

            // then
            assertTrue(result is Result.Success)
            assertEquals(mockLoginResponse.toDomainModel(), (result as Result.Success).data)
        }

    @Test
    fun `login() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() =
        runTest {
            // given
            coEvery {
                dataSource.login(any())
            } returns NetworkResult.Error(error = NetworkErrorType.Network.Conflict, message = mockErrorMessage)

            // when
            val result = repository.login(mockLogin).last()

            // then
            assertTrue(result is Result.Error)
            assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toCommonErrorType())
            assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `existsUser() 성공 테스트`() =
        runTest {
            // given
            coEvery { dataSource.existsUser(any()) } returns NetworkResult.Success(mockExistsResponse)

            // when
            val result = repository.existsUser(mockExistsUser).last()

            // then
            assertTrue(result is Result.Success)
            assertEquals(mockExistsResponse.exists, (result as Result.Success).data)
        }

    @Test
    fun `existsUser() 성공 테스트 - 존재하지 않음(false)`() =
        runTest {
            // given
            coEvery { dataSource.existsUser(any()) } returns NetworkResult.Success(ExistsResponse(false))

            // when
            val result = repository.existsUser(mockExistsUser).last()

            // then
            assertTrue(result is Result.Success)
            assertEquals(false, (result as Result.Success).data)
        }

    @Test
    fun `existsUser() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() =
        runTest {
            // given
            coEvery {
                dataSource.existsUser(any())
            } returns NetworkResult.Error(error = NetworkErrorType.Network.Conflict, message = mockErrorMessage)

            // when
            val result = repository.existsUser(mockExistsUser).last()

            // then
            assertTrue(result is Result.Error)
            assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toCommonErrorType())
            assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `existsDisplayId() 성공 테스트`() =
        runTest {
            // given
            coEvery { dataSource.existsDisplayId(mockDisplayId) } returns NetworkResult.Success(mockExistsResponse)

            // when
            val result = repository.existsDisplayId(mockDisplayId).last()

            // then
            assertTrue(result is Result.Success)
            assertEquals(mockExistsResponse.exists, (result as Result.Success).data)
        }

    @Test
    fun `existsDisplayId() 성공 테스트 - 존재하지 않음(false)`() =
        runTest {
            // given
            coEvery { dataSource.existsDisplayId(mockDisplayId) } returns NetworkResult.Success(ExistsResponse(false))

            // when
            val result = repository.existsDisplayId(mockDisplayId).last()

            // then
            assertTrue(result is Result.Success)
            assertEquals(false, (result as Result.Success).data)
        }

    @Test
    fun `existsDisplayId() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() =
        runTest {
            // given
            coEvery {
                dataSource.existsDisplayId(mockDisplayId)
            } returns NetworkResult.Error(error = NetworkErrorType.Network.Conflict, message = mockErrorMessage)

            // when
            val result = repository.existsDisplayId(mockDisplayId).last()

            // then
            assertTrue(result is Result.Error)
            assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toCommonErrorType())
            assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `register() 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.register(any())
        } returns NetworkResult.Success(mockRegisterResponse)
        coEvery { dataStoreManager.saveLongData(any(), any()) } just Runs

        // when
        val result = repository.register(mockRegister).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(mockRegisterResponse.toDomainModel(), (result as Result.Success).data)
    }

    @Test
    fun `register() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() = runTest {
        // given
        coEvery {
            dataSource.register(any())
        } returns NetworkResult.Error(error = NetworkErrorType.Network.Conflict, message = mockErrorMessage)

        // when
        val result = repository.register(mockRegister).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toCommonErrorType())
        assertEquals(result.message, mockErrorMessage)
    }

    companion object {
        private val mockProviderId = ProviderId("123")
        private val mockUserId = UserId(1L)
        private val mockLogin = Login(SocialLoginProvider.GOOGLE, mockProviderId)
        private val mockAccessToken = "aaa.bbb.ccc"
        private val mockRefreshToken = "rrr.bbb.ccc"
        private val mockLoginResponse =
            LoginResponse(mockUserId.value, mockAccessToken, mockRefreshToken)
        private val mockErrorMessage = "error world!"
        private val mockExistsResponse = ExistsResponse(true)
        private val mockExistsUser = ExistsUser(SocialLoginProvider.GOOGLE, mockProviderId)
        private val mockDisplayId = DisplayId("123")
        private val mockRegisterResponse =
            RegisterResponse(mockUserId.value, mockAccessToken, mockRefreshToken)
        private val mockRegister = Register(
            provider = SocialLoginProvider.GOOGLE,
            providerId = ProviderId("123"),
            displayId = DisplayId("123"),
            name = Name("hong"),
            profileImageUrl = null,
            introduce = null,
        )
    }
}
