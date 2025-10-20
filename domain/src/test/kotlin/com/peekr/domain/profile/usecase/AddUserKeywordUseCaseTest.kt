package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.repository.ProfileRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddUserKeywordUseCaseTest {
    private val profileRepository: ProfileRepository = mockk()
    private val usecase = AddUserKeywordUseCase(profileRepository)

    @Test
    fun `키워드 추가 성공 테스트`() = runTest {
        // given
        every {
            profileRepository.addKeyword(TestKeyword, TestKeywordDescription, any(), any())
        } returns flowOf(Result.Success(TestUserKeyword))

        // when
        val result = usecase(
            keyword = TestKeyword.value,
            description = TestKeywordDescription.value,
        ).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestUserKeyword,
            (result as Result.Success).data,
        )
    }

    companion object {
        private val TestKeyword = KeywordValue("sampleKeyword")
        private val TestKeywordDescription = KeywordDescription("hello")
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = TestKeyword,
            userId = UserId(1L),
            offsetX = 0.0,
            offsetY = 0.0,
            description = TestKeywordDescription,
            createdAt = 1000,
            updatedAt = 1000,
        )
    }
}
