package com.peekr.core.data.repository

import com.peekr.core.data.auth.AuthAppDataCleaner
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.local.error.WritingDataException
import com.peekr.core.data.source.network.datasource.AccountNetworkDataSource
import com.peekr.core.data.source.network.datasource.AuthNetworkDataSource
import com.peekr.core.data.source.network.datasource.UserNetworkDataSource
import com.peekr.core.data.source.network.dto.auth.response.ExistsResponse
import com.peekr.core.data.source.network.dto.auth.response.LoginResponse
import com.peekr.core.data.source.network.dto.auth.response.RegisterResponse
import com.peekr.core.data.source.network.dto.auth.response.toDomainModel
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.model.Register
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {
    private val authNetworkDataSource: AuthNetworkDataSource = mockk()
    private val accountNetworkDataSource: AccountNetworkDataSource = mockk()
    private val userNetworkDataSource: UserNetworkDataSource = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private val authAppDataCleaner: AuthAppDataCleaner = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: AuthRepository =
        AuthRepositoryImpl(
            authNetworkDataSource = authNetworkDataSource,
            accountNetworkDataSource = accountNetworkDataSource,
            userNetworkDataSource = userNetworkDataSource,
            dataStoreManager = dataStoreManager,
            authAppDataCleaner = authAppDataCleaner,
            ioDispatcher = dispatcher,
        )

    @Test
    fun `login() 성공 테스트`() =
        runTest {
            // given
            coEvery { authNetworkDataSource.login(any()) } returns NetworkResult.Success(mockLoginResponse)
            coEvery { dataStoreManager.saveLongData(any(), any()) } just Runs

            // when
            val result = repository.login(mockLoginCredentials).last()

            // then
            Assert.assertTrue(result is Result.Success)
            Assert.assertEquals(mockLoginResponse.toDomainModel(), (result as Result.Success).data)
        }

    @Test
    fun `login() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() =
        runTest {
            // given
            val expectedError = NetworkErrorType.Unexpected(null)
            coEvery {
                authNetworkDataSource.login(any())
            } returns NetworkResult.Error(error = expectedError, message = mockErrorMessage)

            // when
            val result = repository.login(mockLoginCredentials).last()

            // then
            assertTrue(result is Result.Error)
            assertEquals(
                expectedError.toCommonErrorType(),
                (result as Result.Error).error,
            )
            assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `existsUser() 성공 테스트`() =
        runTest {
            // given
            coEvery { authNetworkDataSource.existsUser(any()) } returns NetworkResult.Success(mockExistsResponse)

            // when
            val result = repository.existsUser(mockExistsUser).last()

            // then
            Assert.assertTrue(result is Result.Success)
            Assert.assertEquals(mockExistsResponse.exists, (result as Result.Success).data)
        }

    @Test
    fun `existsUser() 성공 테스트 - 존재하지 않음(false)`() =
        runTest {
            // given
            coEvery { authNetworkDataSource.existsUser(any()) } returns NetworkResult.Success(ExistsResponse(false))

            // when
            val result = repository.existsUser(mockExistsUser).last()

            // then
            Assert.assertTrue(result is Result.Success)
            Assert.assertEquals(false, (result as Result.Success).data)
        }

    @Test
    fun `existsUser() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() =
        runTest {
            // given
            val expectedError = NetworkErrorType.Unexpected(null)
            coEvery {
                authNetworkDataSource.existsUser(any())
            } returns NetworkResult.Error(error = expectedError, message = mockErrorMessage)

            // when
            val result = repository.existsUser(mockExistsUser).last()

            // then
            Assert.assertTrue(result is Result.Error)
            Assert.assertEquals(
                expectedError.toCommonErrorType(),
                (result as Result.Error).error,
            )
            Assert.assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `existsDisplayId() 성공 테스트`() =
        runTest {
            // given
            coEvery { authNetworkDataSource.existsDisplayId(mockDisplayId) } returns NetworkResult.Success(mockExistsResponse)

            // when
            val result = repository.existsDisplayId(mockDisplayId).last()

            // then
            Assert.assertTrue(result is Result.Success)
            Assert.assertEquals(mockExistsResponse.exists, (result as Result.Success).data)
        }

    @Test
    fun `existsDisplayId() 성공 테스트 - 존재하지 않음(false)`() =
        runTest {
            // given
            coEvery { authNetworkDataSource.existsDisplayId(mockDisplayId) } returns NetworkResult.Success(ExistsResponse(false))

            // when
            val result = repository.existsDisplayId(mockDisplayId).last()

            // then
            Assert.assertTrue(result is Result.Success)
            Assert.assertEquals(false, (result as Result.Success).data)
        }

    @Test
    fun `existsDisplayId() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() =
        runTest {
            // given
            val expectedError = NetworkErrorType.Unexpected(null)
            coEvery {
                authNetworkDataSource.existsDisplayId(mockDisplayId)
            } returns NetworkResult.Error(error = expectedError, message = mockErrorMessage)

            // when
            val result = repository.existsDisplayId(mockDisplayId).last()

            // then
            Assert.assertTrue(result is Result.Error)
            Assert.assertEquals(
                expectedError.toCommonErrorType(),
                (result as Result.Error).error,
            )
            Assert.assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `register() 성공 테스트`() = runTest {
        // given
        coEvery {
            authNetworkDataSource.register(any())
        } returns NetworkResult.Success(mockRegisterResponse)
        coEvery { dataStoreManager.saveLongData(any(), any()) } just Runs

        // when
        val result = repository.register(mockRegister).last()

        // then
        Assert.assertTrue(result is Result.Success)
        Assert.assertEquals(mockRegisterResponse.toDomainModel(), (result as Result.Success).data)
    }

    @Test
    fun `register() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            authNetworkDataSource.register(any())
        } returns NetworkResult.Error(error = expectedError, message = mockErrorMessage)

        // when
        val result = repository.register(mockRegister).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
        assertEquals(result.message, mockErrorMessage)
    }

    @Test
    fun `토큰 저장 성공 테스트`() = runTest {
        // given
        val expectedAccessToken = "aaa.bbb.ccc"
        val expectedRefreshToken = "ddd.eee.fff"
        coEvery {
            dataStoreManager.saveEncryptedStringData(
                key = DataStoreKey.Auth.AccessToken,
                value = expectedAccessToken,
            )
        } just Runs
        coEvery {
            dataStoreManager.saveEncryptedStringData(
                key = DataStoreKey.Auth.RefreshToken,
                value = expectedRefreshToken,
            )
        } just Runs

        // when
        val result = repository.saveTokens(expectedAccessToken, expectedRefreshToken).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `토큰 저장 실패 테스트 - WritingDataException 예외 발생 시 정의된 에러를 반환한다`() = runTest {
        // given
        val expectedAccessToken = "aaa.bbb.ccc"
        val expectedRefreshToken = "ddd.eee.fff"
        coEvery {
            dataStoreManager.saveEncryptedStringData(
                key = DataStoreKey.Auth.AccessToken,
                value = expectedAccessToken,
            )
        } throws WritingDataException("", null)

        // when
        val result = repository.saveTokens(expectedAccessToken, expectedRefreshToken).last()

        // then
        val errorResult = result as Result.Error
        assertEquals(
            errorResult.error,
            CommonErrorType.Local.WritingDataFailed,
        )
    }

    companion object {
        private val mockProviderId = ProviderId("123")
        private val mockUserId = UserId.Companion(1L)
        private val mockLoginCredentials = LoginCredentials(SocialLoginProvider.GOOGLE, mockProviderId)
        private val mockAccessToken = "aaa.bbb.ccc"
        private val mockRefreshToken = "rrr.bbb.ccc"
        private val mockLoginResponse =
            LoginResponse(mockUserId.value, mockAccessToken, mockRefreshToken)
        private val mockErrorMessage = "error world!"
        private val mockExistsResponse = ExistsResponse(true)
        private val mockExistsUser = ExistsUser(SocialLoginProvider.GOOGLE, mockProviderId)
        private val mockDisplayId = DisplayId.Companion("123")
        private val mockRegisterResponse =
            RegisterResponse(mockUserId.value, mockAccessToken, mockRefreshToken)
        private val mockRegister = Register(
            provider = SocialLoginProvider.GOOGLE,
            providerId = ProviderId("123"),
            displayId = DisplayId.Companion("123"),
            name = Name.Companion("hong"),
            profileImageUrl = null,
            introduce = null,
        )
    }
}
