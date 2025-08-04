package com.peekr.domain.account.usecase

import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.model.UserUID
import com.peekr.domain.account.util.AuthManager
import com.peekr.domain.account.util.AuthManagerFactory
import com.peekr.domain.shared.util.Result
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
        val userUID = UserUID("google-uid-123")
        val expectedLogin = Login(SocialLoginProvider.GOOGLE, userUID)
        val flow = flowOf(Result.Success(userUID))
        every { authManager.signIn() } returns flow

        // When
        val result = useCase(SocialLoginProvider.GOOGLE).first()

        // Then
        assert(result is Result.Success)
        assert((result as Result.Success).data == expectedLogin)
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
        assert(result is Result.Error)
        assertNotNull((result as Result.Error).message)
        assertTrue(result.message!!.contains(expectedErrorMessage))
    }
}
