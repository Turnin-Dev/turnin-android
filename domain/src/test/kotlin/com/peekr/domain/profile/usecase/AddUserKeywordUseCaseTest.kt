package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.util.Result
import com.peekr.core.domain.validation.CommonValidationException
import com.peekr.core.domain.validation.toValidationErrorType
import com.peekr.domain.profile.error.ProfileErrorType
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

    @Test
    fun `키워드 유효성 검사 실패 시 예외가 발생하고 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedException = CommonValidationException.InvalidFormat("a", "b")
        val expectedError =
            ProfileErrorType.ValidationError(expectedException.toValidationErrorType())
        every {
            profileRepository.addKeyword(TestKeyword, TestKeywordDescription, any(), any())
        } throws expectedException

        // when
        val result = usecase(
            keyword = TestKeyword.value,
            description = TestKeywordDescription.value,
        ).last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError, error.error)
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
