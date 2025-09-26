package com.peekr.data.account.repository

import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.data.network.util.NetworkErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.toErrorType
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.util.Result
import com.peekr.data.account.model.response.ExistsResponse
import com.peekr.data.account.model.response.LoginResponse
import com.peekr.data.account.model.response.PresignedUrlResponse
import com.peekr.data.account.model.response.RegisterResponse
import com.peekr.data.account.network.AccountNetworkDataSource
import com.peekr.domain.account.model.ExistsUser
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.Mime
import com.peekr.domain.account.model.PresignedUrl
import com.peekr.domain.account.model.ProviderId
import com.peekr.domain.account.model.Register
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.repository.AccountRepository
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
    private val dataStoreManager: DataStoreManager = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: AccountRepository =
        AccountRepositoryImpl(dataSource, dataStoreManager, dispatcher)

    @Test
    fun `login() 성공 테스트`() =
        runTest {
            // given
            coEvery { dataSource.login(any()) } returns NetworkResult.Success(mockLoginResponse)

            // when
            val result = repository.login(mockLogin).last()

            // then
            assertTrue(result is Result.Success)
            assertEquals((result as Result.Success).data, mockJWTToken)
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
            assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toErrorType())
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
            assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toErrorType())
            assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `existsDisplayId() 성공 테스트`() =
        runTest {
            // given
            coEvery { dataSource.existsDisplayId(any()) } returns NetworkResult.Success(mockExistsResponse)

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
            coEvery { dataSource.existsDisplayId(any()) } returns NetworkResult.Success(ExistsResponse(false))

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
                dataSource.existsDisplayId(any())
            } returns NetworkResult.Error(error = NetworkErrorType.Network.Conflict, message = mockErrorMessage)

            // when
            val result = repository.existsDisplayId(mockDisplayId).last()

            // then
            assertTrue(result is Result.Error)
            assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toErrorType())
            assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `getFileUploadPresignedUrl() 성공 테스트`() =
        runTest {
            // given
            coEvery {
                dataSource.getFileUploadPresignedUrl(any(), any())
            } returns NetworkResult.Success(mockPresignedUrlResponse)

            // when
            val result = repository.getFileUploadPresignedUrl("a.jpg", Mime.IMAGE_JPEG).last()

            // then
            assertTrue(result is Result.Success)
            assertEquals(mockPresignedUrl, (result as Result.Success).data)
        }

    @Test
    fun `getFileUploadPresignedUrl() 실패 테스트 - 데이터 소스에서 에러 방출 시 Error를 반환한다`() =
        runTest {
            // given
            coEvery {
                dataSource.getFileUploadPresignedUrl(any(), any())
            } returns NetworkResult.Error(error = NetworkErrorType.Network.Conflict, message = mockErrorMessage)

            // when
            val result = repository.getFileUploadPresignedUrl("a.jpg", Mime.IMAGE_JPEG).last()

            // then
            assertTrue(result is Result.Error)
            assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toErrorType())
            assertEquals(result.message, mockErrorMessage)
        }

    @Test
    fun `register() 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.register(any())
        } returns NetworkResult.Success(mockRegisterResponse)

        // when
        val result = repository.register(mockRegister).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(mockJWTToken, (result as Result.Success).data)
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
        assertEquals((result as Result.Error).error, NetworkErrorType.Network.Conflict.toErrorType())
        assertEquals(result.message, mockErrorMessage)
    }

    companion object {
        private val mockProviderId = ProviderId("123")
        private val mockLogin = Login(SocialLoginProvider.GOOGLE, mockProviderId)
        private val mockAccessToken = "aaa.bbb.ccc"
        private val mockRefreshToken = "rrr.bbb.ccc"
        private val mockLoginResponse = LoginResponse(mockAccessToken, mockRefreshToken)
        private val mockJWTToken = JWTToken(mockAccessToken, mockRefreshToken)
        private val mockErrorMessage = "error world!"
        private val mockExistsResponse = ExistsResponse(true)
        private val mockNotExistsResponse = ExistsResponse(false)
        private val mockExistsUser = ExistsUser(SocialLoginProvider.GOOGLE, mockProviderId)
        private val mockDisplayId = DisplayId("123")
        private val mockPresignedUrlResponse = PresignedUrlResponse("example.com", "PUT", 600)
        private val mockPresignedUrl = PresignedUrl("example.com", "PUT", 600)
        private val mockRegisterResponse = RegisterResponse(mockAccessToken, mockRefreshToken)
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
