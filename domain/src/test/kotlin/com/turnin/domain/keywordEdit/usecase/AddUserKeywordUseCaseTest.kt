package com.turnin.domain.keywordEdit.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.core.domain.userKeyword.model.CreateUserKeyword
import com.turnin.core.domain.userKeyword.model.UserKeyword
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.core.domain.util.DomainLogger
import com.turnin.domain.keywordEdit.error.KeywordEditErrorType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AddUserKeywordUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val logger: DomainLogger = mockk()
    private val usecase = AddUserKeywordUseCase(userRepository, userKeywordRepository, logger)

    @Before
    fun setUp() {
        every { logger.e(any(), any(), any()) } just Runs
        coEvery { userRepository.getMyUserId() } returns TestUserId
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
        Assert.assertEquals(TestUserKeyword, success.data)
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
        Assert.assertTrue(error.error is KeywordEditErrorType.ValidationError)
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestKeyword = KeywordName("sampleKeyword")
        private val TestKeywordDescription = KeywordDescription("hello")
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = TestKeyword,
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
