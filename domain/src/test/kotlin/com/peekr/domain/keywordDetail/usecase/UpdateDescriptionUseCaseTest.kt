package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateDescriptionUseCaseTest {
    private val userKeywordRepository: UserKeywordRepository = mockk()
    private val usecase = UpdateDescriptionUseCase(userKeywordRepository)

    @Test
    fun `키워드 설명 수정 성공 테스트`() = runTest {
        // given
        every {
            userKeywordRepository.patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescription = TestPatchDescription,
            )
        } returns flowOf(Result.Success(TestPatchDescription))

        // when
        val result = usecase(TestUserKeywordId.value, TestDescription.value).last()

        // then
        val success = result as Result.Success
        assertEquals(TestPatchDescription, success.data)
    }

    companion object {
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestDescription = KeywordDescription("sample")
        private val TestPatchDescription = PatchDescription(TestDescription)
    }
}
