package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.domain.login.util.AuthManager
import com.peekr.domain.login.util.AuthManagerFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SocialLoginUseCaseTest {
    private lateinit var useCase: SocialLoginUseCase

    private val authManagerFactory: AuthManagerFactory = mockk()
    private val authManager: AuthManager = mockk()

    @Before
    fun setup() {
        every { authManagerFactory.create(any()) } returns authManager
        useCase = SocialLoginUseCase(authManagerFactory)
    }

    @Test
    fun `invoke returns Success when authManager returns Success`() = runTest {
        // Given
        val providerId = ProviderId("google-uid-123")
        val expectedLoginCredentials = LoginCredentials(SocialLoginProvider.GOOGLE, providerId)
        val flow = flowOf(Result.Success(providerId))
        every { authManager.signIn() } returns flow

        // When
        val result = useCase(SocialLoginProvider.GOOGLE).first()

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data == expectedLoginCredentials)
    }

    @Test
    fun `invoke catch exception when authManager throws exception`() = runTest {
        // Given
        val expectedErrorMessage = "Error!"
        every { authManager.signIn() } returns flow {
            throw IllegalStateException(expectedErrorMessage)
        }

        // When
        val result = useCase(SocialLoginProvider.GOOGLE).first()

        // Then
        assertTrue(result is Result.Error)
        assertNotNull((result as Result.Error).message)
        assertTrue(result.message!!.contains(expectedErrorMessage))
    }
}
