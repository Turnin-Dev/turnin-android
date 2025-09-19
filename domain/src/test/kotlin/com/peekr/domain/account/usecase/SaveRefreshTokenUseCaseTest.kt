package com.peekr.domain.account.usecase

import com.peekr.domain.common.dataStore.DataStoreManager
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveRefreshTokenUseCaseTest {
    private val dataStoreManager: DataStoreManager = mockk()
    private val usecase = SaveRefreshTokenUseCase(dataStoreManager)

    @Test
    fun `리프레쉬 토큰 저장 성공 테스트`() = runTest {
        // given
        coEvery {
            dataStoreManager.saveEncryptedStringData(any(), any())
        } returns Unit

        // when
        val result = usecase(TEST_REFRESH_TOKEN).last()

        // then
        assert(result is Result.Success)
        assert((result as Result.Success).data)
    }

    @Test
    fun `리프레쉬 토큰 저장 시 예외가 발생하면 정상적으로 에러를 반환한다`() = runTest {
        // given
        coEvery {
            dataStoreManager.saveEncryptedStringData(any(), any())
        } throws Exception()

        // when
        val result = usecase(TEST_REFRESH_TOKEN).last()

        // then
        assert(result is Result.Error)
        assert((result as Result.Error).error is ErrorType.Unexpected)
    }

    companion object {
        private const val TEST_REFRESH_TOKEN = "aaa.bbb.ccc"
    }
}
