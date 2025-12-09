package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.model.CoreMyProfile
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMyProfileUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = GetMyProfileUseCase(userRepository, userKeywordRepository)

    @Before
    fun setUp() {
        every {
            userRepository.getMyProfile()
        } returns flowOf(Result.Success(TestCoreMyProfile))
        every {
            userKeywordRepository.getUserKeywords(TestMyUserId)
        } returns flowOf(Result.Success(TestUserKeywords))
        coEvery {
            userRepository.getUserId()
        } returns TestMyUserId
    }

    @Test
    fun `나의 프로필 조회 성공 테스트`() = runTest {
        // when
        val result = usecase().last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywords.keywords, success.data.keywords)
        assertEquals(TestCoreMyProfile.displayId, success.data.displayId)
    }

    companion object {
        private val TestMyUserId = UserId(1L)
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = KeywordValue("key"),
            userId = UserId(1L),
            offsetX = 0.0,
            offsetY = 0.0,
            description = KeywordDescription("hello"),
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywords = UserKeywords(listOf(TestUserKeyword))
        private val TestCoreMyProfile = CoreMyProfile(
            userId = TestMyUserId,
            displayId = DisplayId("did"),
            name = Name("name"),
            profileImageUrl = null,
            introduce = Introduce("hello"),
            lastLoginAt = 1000L,
            friendsCount = 50L,
            active = true,
        )
    }
}
