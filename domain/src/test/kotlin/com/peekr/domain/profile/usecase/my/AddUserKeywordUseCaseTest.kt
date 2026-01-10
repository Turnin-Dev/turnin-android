package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.keywordEdit.usecase.AddUserKeywordUseCase
import com.peekr.domain.profile.error.ProfileErrorType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddUserKeywordUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = AddUserKeywordUseCase(userRepository, userKeywordRepository)

    @Before
    fun setUp() {
        coEvery { userRepository.getUserId() } returns TestUserId
        every {
            userKeywordRepository.createUserKeyword(TestCreateUserKeyword)
        } returns flowOf(Result.Success(TestUserKeyword))
    }

    @Test
    fun `키워드 추가 성공 테스트`() = runTest {
        // when
        val result = usecase(
            keyword = TestKeyword.value,
            description = TestKeywordDescription.value,
        ).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeyword, success.data)
    }

    @Test
    fun `키워드 유효성 검사 실패 시 예외가 발생하고 정상적으로 에러를 반환한다`() = runTest {
        // when
        val result = usecase(
            keyword = "a".repeat(KeywordName.MAX_LENGTH + 1),
            description = TestKeywordDescription.value,
        ).last()

        // then
        val error = result as Result.Error
        assertTrue(error.error is ProfileErrorType.ValidationError)
    }

    companion object {
        private const val INITIAL_OFFSET_X = 0.0
        private const val INITIAL_OFFSET_Y = 0.0
        private val TestUserId = UserId(1L)
        private val TestKeyword = KeywordName("sampleKeyword")
        private val TestKeywordDescription = KeywordDescription("hello")
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = TestKeyword,
            userId = TestUserId,
            description = TestKeywordDescription,
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestCreateUserKeyword = CreateUserKeyword(
            userId = TestUserId,
            keyword = TestKeyword,
            description = TestKeywordDescription,
        )
    }
}
