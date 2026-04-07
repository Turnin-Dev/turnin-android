package com.peekr.domain.block.usecase

import com.peekr.core.domain.block.model.BlockReasonId
import com.peekr.core.domain.block.model.CreateBlock
import com.peekr.core.domain.block.repository.BlockRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.block.error.BlockErrorType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBlockUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val blockRepository: BlockRepository = mockk()

    private lateinit var usecase: CreateBlockUseCase

    @Before
    fun setUp() {
        usecase = CreateBlockUseCase(
            userRepository = userRepository,
            blockRepository = blockRepository,
        )
    }

    // ==================================================
    // MissingBlockTarget 에러
    // ==================================================

    @Test
    fun `myUserId가 null이면 MissingBlockTarget 에러를 반환한다`() = runTest {
        // given
        coEvery { userRepository.getMyUserId() } returns null

        // when
        val result = usecase(blockedId = 2L, reasonId = 1L, customReason = null).first()

        // then
        assertEquals(Result.Error(BlockErrorType.MissingBlockTarget), result)
    }

    @Test
    fun `blockedId가 null이면 MissingBlockTarget 에러를 반환한다`() = runTest {
        // given
        coEvery { userRepository.getMyUserId() } returns UserId(1L)

        // when
        val result = usecase(blockedId = null, reasonId = 1L, customReason = null).first()

        // then
        assertEquals(Result.Error(BlockErrorType.MissingBlockTarget), result)
    }

    @Test
    fun `reasonId가 null이면 MissingBlockTarget 에러를 반환한다`() = runTest {
        // given
        coEvery { userRepository.getMyUserId() } returns UserId(1L)

        // when
        val result = usecase(blockedId = 2L, reasonId = null, customReason = null).first()

        // then
        assertEquals(Result.Error(BlockErrorType.MissingBlockTarget), result)
    }

    // ==================================================
    // 차단 성공
    // ==================================================

    @Test
    fun `차단 성공 시 Success를 반환한다`() = runTest {
        // given
        coEvery { userRepository.getMyUserId() } returns UserId(1L)
        every { blockRepository.createBlock(any()) } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(blockedId = 2L, reasonId = 1L, customReason = null).first()

        // then
        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `customReason이 있을 때 차단 성공 시 Success를 반환한다`() = runTest {
        // given
        coEvery { userRepository.getMyUserId() } returns UserId(1L)
        every { blockRepository.createBlock(any()) } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(blockedId = 2L, reasonId = 1L, customReason = "기타 사유").first()

        // then
        assertEquals(Result.Success(Unit), result)
    }

    // ==================================================
    // 차단 실패 - 에러 매핑
    // ==================================================

    @Test
    fun `Forbidden 에러 발생 시 RequesterIdBlockerIdNotSame 에러로 매핑된다`() = runTest {
        // given
        coEvery { userRepository.getMyUserId() } returns UserId(1L)
        every { blockRepository.createBlock(any()) } returns flowOf(
            Result.Error(CommonErrorType.Network.Forbidden),
        )

        // when
        val result = usecase(blockedId = 2L, reasonId = 1L, customReason = null).first()

        // then
        assertEquals(Result.Error(BlockErrorType.RequesterIdBlockerIdNotSame), result)
    }

    @Test
    fun `Forbidden 외 에러 발생 시 CommonError로 매핑된다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        coEvery { userRepository.getMyUserId() } returns UserId(1L)
        every { blockRepository.createBlock(any()) } returns flowOf(
            Result.Error(commonError),
        )

        // when
        val result = usecase(blockedId = 2L, reasonId = 1L, customReason = null).first()

        // then
        assertEquals(Result.Error(BlockErrorType.CommonError(commonError)), result)
    }

    // ==================================================
    // 차단 요청 모델 검증
    // ==================================================

    @Test
    fun `차단 요청 시 올바른 CreateBlock 모델로 blockRepository를 호출한다`() = runTest {
        // given
        val myUserId = UserId(1L)
        val blockedId = 2L
        val reasonId = 3L
        val customReason = "기타 사유"
        val expectedCreateBlock = CreateBlock(
            blockerId = myUserId,
            blockedId = UserId(blockedId),
            reasonId = BlockReasonId(reasonId),
            customReason = customReason,
        )

        coEvery { userRepository.getMyUserId() } returns myUserId
        every { blockRepository.createBlock(expectedCreateBlock) } returns flowOf(Result.Success(Unit))

        // when
        usecase(blockedId = blockedId, reasonId = reasonId, customReason = customReason).first()

        // then
        verify { blockRepository.createBlock(expectedCreateBlock) }
    }
}
