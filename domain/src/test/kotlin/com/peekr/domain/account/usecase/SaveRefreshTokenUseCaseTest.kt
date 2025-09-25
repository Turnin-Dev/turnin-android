package com.peekr.domain.account.usecase

import com.peekr.core.domain.util.Result
import com.peekr.domain.account.repository.AccountRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveRefreshTokenUseCaseTest {
    private val accountRepository: AccountRepository = mockk()
    private val usecase = SaveRefreshTokenUseCase(accountRepository)

    @Test
    fun `리프레쉬 토큰 저장 성공 테스트`() = runTest {
        // given
        coEvery {
            accountRepository.saveRefreshToken(any())
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(TEST_REFRESH_TOKEN).last()

        // then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)
    }

    companion object {
        private const val TEST_REFRESH_TOKEN = "aaa.bbb.ccc"
    }
}
