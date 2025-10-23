package com.peekr.data.profile.repository

import com.peekr.core.data.datastore.DataStoreManager
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.Role
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.model.UserProfile
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.error.UserKeywordErrorType
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.ProfilePatch
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileRepositoryImplTest {
    private val userRepository: UserRepository = mockk()
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private val repository =
        ProfileRepositoryImpl(userRepository, userKeywordRepository, dataStoreManager)

    @Test
    fun `사용자 프로필 조회 - 성공 테스트`() = runTest {
        // given
        every { userRepository.getUserProfile() } returns
            flow {
                emit(Result.Loading)
                emit(Result.Success(TestUserProfile))
            }
        every { userKeywordRepository.getUserKeywords() } returns
            flow {
                emit(Result.Loading)
                emit(Result.Success(TestUserKeywords))
            }

        // when
        val results = repository.getProfile().toList()

        // then
        assertTrue(results.size >= 2) // 로딩, 성공/에러 데이터가 방출되므로 최소 2개 이상
        assertTrue(results.last() is Result.Success)
        assertEquals(
            TestUserProfile.user.displayId,
            (results.last() as Result.Success).data.displayId,
        )
        assertEquals(
            TestUserProfile.user.name,
            (results.last() as Result.Success).data.name,
        )
    }

    @Test
    fun `사용자 프로필 조회 - 사용자 조회할 때 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = UserErrorType.Unexpected(null)
        every { userRepository.getUserProfile() } returns
            flow {
                emit(Result.Loading)
                emit(Result.Error(expectedError))
            }
        every { userKeywordRepository.getUserKeywords() } returns
            flow {
                emit(Result.Loading)
                emit(Result.Success(TestUserKeywords))
            }

        // when
        val results = repository.getProfile().toList()

        // then
        assertTrue(results.size >= 2) // 로딩, 성공/에러 데이터가 방출되므로 최소 2개 이상
        assertTrue(results.last() is Result.Error)
        assertEquals(expectedError, (results.last() as Result.Error).error)
    }

    @Test
    fun `사용자 프로필 조회 - 사용자 키워드 조회할 때 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = UserKeywordErrorType.Unexpected(null)
        every { userRepository.getUserProfile() } returns
            flow {
                emit(Result.Loading)
                emit(Result.Success(TestUserProfile))
            }
        every { userKeywordRepository.getUserKeywords() } returns
            flow {
                emit(Result.Loading)
                emit(Result.Error(expectedError))
            }

        // when
        val results = repository.getProfile().toList()

        // then
        assertTrue(results.size >= 2) // 로딩, 성공/에러 데이터가 방출되므로 최소 2개 이상
        assertTrue(results.last() is Result.Error)
        assertEquals(expectedError, (results.last() as Result.Error).error)
    }

    @Test
    fun `사용자 프로필 수정 - 성공 테스트`() = runTest {
        // given
        every {
            userRepository.updateUser(TestUserPatch)
        } returns flowOf(Result.Success(Unit))

        // when
        val result = repository.updateProfile(TestProfilePatch).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 프로필 수정 - 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = UserErrorType.Unexpected(null)
        every {
            userRepository.updateUser(TestUserPatch)
        } returns flowOf(Result.Error(expectedError))

        // when
        val result = repository.updateProfile(TestProfilePatch).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, (result as Result.Error).error)
    }

    @Test
    fun `사용자 프로필 키워드 추가 - 성공 테스트`() = runTest {
        // given
        every { dataStoreManager.getLongData(any()) } returns flowOf(TestUserId.value)
        every {
            userKeywordRepository.createUserKeyword(any())
        } returns flowOf(Result.Success(TestUserKeyword))

        // when
        val addedUserKeyword = repository
            .addKeyword(
                TestCreateUserKeyword.keyword,
                TestCreateUserKeyword.description,
                TestCreateUserKeyword.offsetX,
                TestCreateUserKeyword.offsetY,
            ).last()

        // then
        assertTrue(addedUserKeyword is Result.Success)
        assertEquals(
            TestUserKeyword,
            (addedUserKeyword as Result.Success).data,
        )
    }

    @Test
    fun `사용자 프로필 키워드 추가 - 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = UserKeywordErrorType.Unexpected(null)
        every { dataStoreManager.getLongData(any()) } returns flowOf(TestUserId.value)
        every {
            userKeywordRepository.createUserKeyword(any())
        } returns flowOf(Result.Error(expectedError))

        // when
        val addedUserKeyword = repository
            .addKeyword(
                TestCreateUserKeyword.keyword,
                TestCreateUserKeyword.description,
                TestCreateUserKeyword.offsetX,
                TestCreateUserKeyword.offsetY,
            ).last()

        // then
        assertTrue(addedUserKeyword is Result.Error)
        assertEquals(
            expectedError,
            (addedUserKeyword as Result.Error).error,
        )
    }

    @Test
    fun `사용자 프로필 키워드 추가 - 사용자 ID가 존재하지 않는 경우 정상적으로 에러를 반환한다`() = runTest {
        // given
        every { dataStoreManager.getLongData(any()) } returns flowOf(null)
        every {
            userKeywordRepository.createUserKeyword(any())
        } returns flowOf(Result.Success(TestUserKeyword))

        // when
        val addedUserKeyword = repository
            .addKeyword(
                TestCreateUserKeyword.keyword,
                TestCreateUserKeyword.description,
                TestCreateUserKeyword.offsetX,
                TestCreateUserKeyword.offsetY,
            ).last()

        // then
        assertTrue(addedUserKeyword is Result.Error)
        assertEquals(
            ProfileErrorType.Unexpected(null),
            (addedUserKeyword as Result.Error).error,
        )
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestKeyword = KeywordValue("sampleKeyword")
        private val TestKeywordDescription = KeywordDescription("sample")
        private val TestUserProfile = UserProfile(
            user = User(
                id = TestUserId,
                role = Role.USER,
                provider = SocialLoginProvider.GOOGLE,
                providerId = ProviderId("g1"),
                displayId = DisplayId("id"),
                name = Name("name"),
                profileImageUrl = null,
                introduce = Introduce("hello"),
                lastLoginAt = 1000L,
                active = true,
            ),
            friendsCount = 40,
        )
        private val TestUserKeyword = UserKeyword(
            id = UserKeywordId(1L),
            keywordId = KeywordId(1L),
            keyword = TestKeyword,
            userId = TestUserId,
            offsetX = 50.0,
            offsetY = 50.0,
            description = TestKeywordDescription,
            createdAt = 1000L,
            updatedAt = 1000L,
        )
        private val TestUserKeywords = UserKeywords(
            keywords = listOf(TestUserKeyword),
        )
        private val TestUserPatch = UserPatch(
            displayId = DisplayId("id"),
            name = Name("name"),
            profileImageUrl = null,
            introduce = Introduce("hello"),
        )
        private val TestProfilePatch = ProfilePatch(
            displayId = DisplayId("id"),
            name = Name("name"),
            profileImageUrl = null,
            introduce = Introduce("hello"),
        )
        private val TestCreateUserKeyword = CreateUserKeyword(
            userId = TestUserId,
            keyword = TestKeyword,
            description = TestKeywordDescription,
            offsetX = 0.0,
            offsetY = 0.0,
        )
    }
}
