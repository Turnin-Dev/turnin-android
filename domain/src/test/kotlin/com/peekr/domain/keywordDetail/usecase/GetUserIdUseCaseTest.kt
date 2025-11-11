package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.util.Result
import com.peekr.domain.keywordDetail.repository.KeywordDetailRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetUserIdUseCaseTest {
    private val keywordDetailRepository: KeywordDetailRepository = mockk()
    private val usecase = GetUserIdUseCase(keywordDetailRepository)

    @Test
    fun `사용자 ID 조회 성공 테스트`() = runTest {
        // given
        every {
            keywordDetailRepository.getUserId()
        } returns flowOf(Result.Success(TestUserId))

        // when
        val result = usecase().last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserId, success.data)
    }

    companion object {
        private val TestUserId = UserId(1L)
    }
}
