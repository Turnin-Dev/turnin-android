package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.usecase.GetMyUserIdUseCase
import com.peekr.core.domain.userKeyword.model.UserInfo
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.DomainLogger
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.model.toKeywordDetail
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetKeywordDetailUseCaseTest {
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val getMyUserIdUseCase: GetMyUserIdUseCase = mockk()
    private val logger: DomainLogger = mockk()
    private val usecase = GetKeywordDetailUseCase(userKeywordRepository, getMyUserIdUseCase, logger)

    @Before
    fun setUp() {
        coEvery { getMyUserIdUseCase() } returns TestUserId
        every { logger.e(any(), any(), any()) } just Runs
    }

    // ------------------------------ 나의 키워드를 조회하는 경우 ------------------------------
    @Test
    fun `나의 키워드 상세 정보가 로컬 DB에 있다면 해당 데이터를 그대로 방출한다`() = runTest {
        // given
        every {
            userKeywordRepository.getMyDetailFromLocal(TestUserKeywordId)
        } returns flowOf(TestUserKeywordDetail)

        // when
        val result = usecase(TestUserId.value, TestUserKeywordId.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywordDetail.toKeywordDetail(), success.data)
    }

    @Test
    fun `나의 키워드 상세 정보가 로컬 DB에 없다면 네트워크에서 조회하여 가져온다`() = runTest {
        // given
        every { userKeywordRepository.getMyDetailFromLocal(TestUserKeywordId) } returns flowOf(null)
        every {
            userKeywordRepository.getDetailRefresh(TestUserId, TestUserKeywordId)
        } returns flowOf(Result.Success(TestUserKeywordDetail))

        // when
        val result = usecase(TestUserId.value, TestUserKeywordId.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywordDetail.toKeywordDetail(), success.data)
    }

    @Test
    fun `나의 키워드 조회 시 에러 발생 시 도메인 에러로 변환하여 정상적으로 방출한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        every { userKeywordRepository.getMyDetailFromLocal(TestUserKeywordId) } returns flowOf(null)
        every {
            userKeywordRepository.getDetailRefresh(TestUserId, TestUserKeywordId)
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase(TestUserId.value, TestUserKeywordId.value).last()

        // then
        val error = result as Result.Error
        assertTrue(error.error is KeywordDetailErrorType.CommonError)
    }

    // ------------------------------ 사용자 키워드를 조회하는 경우 ------------------------------

    @Test
    fun `사용자 키워드 상세 정보를 성공적으로 조회한다`() = runTest {
        // given
        every {
            userKeywordRepository.getDetail(TestOtherUserId, TestOtherUserKeywordId)
        } returns flowOf(Result.Success(TestOtherUserKeywordDetail))

        // when
        val result = usecase(TestOtherUserId.value, TestOtherUserKeywordId.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestOtherUserKeywordDetail.toKeywordDetail(), success.data)
    }

    @Test
    fun `사용자 키워드 조회 시 에러 발생 시 도메인 에러로 변환하여 정상적으로 방출한다`() = runTest {
        // given
        val expectedError = CommonErrorType.Unexpected(null)
        every {
            userKeywordRepository.getDetail(TestOtherUserId, TestOtherUserKeywordId)
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = usecase(TestOtherUserId.value, TestOtherUserKeywordId.value).last()

        // then
        val error = result as Result.Error
        assertTrue(error.error is KeywordDetailErrorType.CommonError)
    }

    companion object {
        // ------------------------------ 나의 키워드 테스트 데이터 ------------------------------
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestUserKeywordDetail = UserKeywordDetail(
            userKeywordId = TestUserKeywordId,
            keywordId = KeywordId(1L),
            keywordName = KeywordName("k_name"),
            description = KeywordDescription("desc"),
            userInfo = UserInfo(
                userId = TestUserId,
                userName = Name("name"),
                profileImageUrl = null,
            ),
            createdAt = 0L,
            updatedAt = 0L,
        )

        // ------------------------------ 사용자 키워드 테스트 데이터 ------------------------------
        private val TestOtherUserId = UserId(100L)
        private val TestOtherUserKeywordId = UserKeywordId(100L)
        private val TestOtherUserKeywordDetail = UserKeywordDetail(
            userKeywordId = TestOtherUserKeywordId,
            keywordId = KeywordId(1L),
            keywordName = KeywordName("k_name"),
            description = KeywordDescription("desc"),
            userInfo = UserInfo(
                userId = TestOtherUserId,
                userName = Name("name"),
                profileImageUrl = null,
            ),
            createdAt = 0L,
            updatedAt = 0L,
        )
    }
}
