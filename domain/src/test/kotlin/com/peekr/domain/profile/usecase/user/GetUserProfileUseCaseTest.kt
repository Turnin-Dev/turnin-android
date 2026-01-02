package com.peekr.domain.profile.usecase.user

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.model.CoreUserProfile
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetUserProfileUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = GetUserProfileUseCase(userRepository, userKeywordRepository)

    @Before
    fun setUp() {
        every {
            userRepository.getUserProfile(TestUserId)
        } returns flowOf(Result.Success(TestCoreUserProfile))
        every {
            userKeywordRepository.getUserKeywords(TestUserId)
        } returns flowOf(Result.Success(TestUserKeywords))
    }

    @Test
    fun `사용자 프로필 조회 성공 테스트`() = runTest {
        // when
        val result = usecase(TestUserId.value).last()

        // then
        val success = result as Result.Success
        Assert.assertEquals(TestUserKeywords.keywords, success.data.keywords)
        Assert.assertEquals(TestCoreUserProfile.friendStatus, success.data.friendStatus)
    }

    companion object {
        private val TestUserId = UserId.Companion(1L)
        private val TestDisplayId = DisplayId.Companion("did")
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId.Companion(1L),
            keywordId = KeywordId.Companion(1L),
            keyword = KeywordName.Companion("key"),
            userId = TestUserId,
            description = KeywordDescription.Companion("hello"),
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywords = UserKeywords(listOf(TestUserKeyword))
        private val TestCoreUserProfile = CoreUserProfile(
            userId = TestUserId,
            displayId = TestDisplayId,
            name = Name.Companion("name"),
            profileImageUrl = null,
            introduce = Introduce.Companion("hello"),
            lastLoginAt = 1000L,
            friendsCount = 50L,
            active = true,
            friendStatus = FriendStatus.NOTHING,
        )
    }
}
