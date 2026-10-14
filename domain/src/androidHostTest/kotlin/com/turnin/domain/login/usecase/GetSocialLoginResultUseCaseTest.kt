package com.turnin.domain.login.usecase

import com.turnin.core.domain.auth.model.ExistsUser
import com.turnin.core.domain.auth.model.LoginCredentials
import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.auth.social.SocialAuthManager
import com.turnin.core.domain.auth.social.SocialAuthManagerFactory
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.ProviderId
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.domain.login.error.LoginErrorType
import com.turnin.domain.login.model.LoginWithExistsUser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetSocialLoginResultUseCaseTest {
    private val socialAuthManagerFactory: SocialAuthManagerFactory = mockk()
    private val authRepository: AuthRepository = mockk()
    private val socialAuthManager: SocialAuthManager = mockk()

    private val usecase = GetSocialLoginResultUseCase(
        socialAuthManagerFactory = socialAuthManagerFactory,
        authRepository = authRepository,
    )

    private val provider = SocialLoginProvider.KAKAO
    private val providerId = ProviderId("test_provider_id")
    private val loginCredentials = LoginCredentials(provider = provider, providerId = providerId)
    private val existsUser = ExistsUser(provider = provider, providerId = providerId)

    @Before
    fun setUp() {
        every { socialAuthManagerFactory.create(provider) } returns socialAuthManager
    }

    @Test
    fun `소셜 로그인 성공 후 사용자가 존재하면 isExistsUser가 true인 LoginWithExistsUser를 반환한다`() = runTest {
        // given
        every { socialAuthManager.signIn() } returns flowOf(Result.Success(providerId))
        every { authRepository.existsUser(existsUser) } returns flowOf(Result.Success(true))

        // when
        val results = usecase(provider).toList()

        // then
        assertEquals(
            listOf(
                Result.Loading,
                Result.Success(
                    LoginWithExistsUser(
                        loginCredentials = loginCredentials,
                        isExistsUser = true,
                    ),
                ),
            ),
            results,
        )
    }

    @Test
    fun `소셜 로그인 성공 후 사용자가 존재하지 않으면 isExistsUser가 false인 LoginWithExistsUser를 반환한다`() = runTest {
        // given
        every { socialAuthManager.signIn() } returns flowOf(Result.Success(providerId))
        every { authRepository.existsUser(existsUser) } returns flowOf(Result.Success(false))

        // when
        val results = usecase(provider).toList()

        // then
        assertEquals(
            listOf(
                Result.Loading,
                Result.Success(
                    LoginWithExistsUser(
                        loginCredentials = loginCredentials,
                        isExistsUser = false,
                    ),
                ),
            ),
            results,
        )
    }

    @Test
    fun `소셜 로그인 실패 시 Error를 반환한다`() = runTest {
        // given
        val error = CommonErrorType.Unexpected(null)
        every { socialAuthManager.signIn() } returns flowOf(Result.Error(error))

        // when
        val results = usecase(provider).toList()

        // then
        assertEquals(
            listOf(
                Result.Loading,
                Result.Error(LoginErrorType.CommonError(error)),
            ),
            results,
        )
    }

    @Test
    fun `사용자 존재 여부 확인 실패 시 CommonError를 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        every { socialAuthManager.signIn() } returns flowOf(Result.Success(providerId))
        every { authRepository.existsUser(existsUser) } returns flowOf(Result.Error(commonError))

        // when
        val results = usecase(provider).toList()

        // then
        assertEquals(
            listOf(
                Result.Loading,
                Result.Error(LoginErrorType.CommonError(commonError)),
            ),
            results,
        )
    }

    @Test
    fun `예외 발생 시 LoginFailed Error를 반환한다`() = runTest {
        // given
        val errorMessage = "Unexpected error"
        every { socialAuthManager.signIn() } returns flow { throw RuntimeException(errorMessage) }

        // when
        val results = usecase(provider).toList()

        // then
        assertEquals(
            listOf(
                Result.Loading,
                Result.Error(LoginErrorType.LoginFailed, errorMessage),
            ),
            results,
        )
    }

    @Test
    fun `첫 번째 emit은 항상 Loading이다`() = runTest {
        // given
        every { socialAuthManager.signIn() } returns flowOf(Result.Success(providerId))
        every { authRepository.existsUser(existsUser) } returns flowOf(Result.Success(true))

        // when
        val firstResult = usecase(provider).first()

        // then
        assertEquals(Result.Loading, firstResult)
    }
}
