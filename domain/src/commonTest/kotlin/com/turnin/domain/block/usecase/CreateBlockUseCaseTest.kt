package com.turnin.domain.block.usecase

import com.turnin.core.domain.block.model.BlockReasonId
import com.turnin.core.domain.block.model.CreateBlock
import com.turnin.core.domain.block.repository.BlockRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.block.error.BlockErrorType
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBlockUseCaseTest {
    private val userRepository: UserRepository = mock()
    private val blockRepository: BlockRepository = mock()

    private lateinit var usecase: CreateBlockUseCase

    @BeforeTest
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
        everySuspend { userRepository.getMyUserId() } returns null

        // when
        val result = usecase(blockedId = 2L, reasonId = 1L, customReason = null).first()

        // then
        assertEquals(Result.Error(BlockErrorType.MissingBlockTarget), result)
    }

    @Test
    fun `blockedId가 null이면 MissingBlockTarget 에러를 반환한다`() = runTest {
        // given
        everySuspend { userRepository.getMyUserId() } returns UserId(1L)

        // when
        val result = usecase(blockedId = null, reasonId = 1L, customReason = null).first()

        // then
        assertEquals(Result.Error(BlockErrorType.MissingBlockTarget), result)
    }

    @Test
    fun `reasonId가 null이면 MissingBlockTarget 에러를 반환한다`() = runTest {
        // given
        everySuspend { userRepository.getMyUserId() } returns UserId(1L)

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
        everySuspend { userRepository.getMyUserId() } returns UserId(1L)
        every { blockRepository.createBlock(any()) } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(blockedId = 2L, reasonId = 1L, customReason = null).first()

        // then
        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `customReason이 있을 때 차단 성공 시 Success를 반환한다`() = runTest {
        // given
        everySuspend { userRepository.getMyUserId() } returns UserId(1L)
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
        everySuspend { userRepository.getMyUserId() } returns UserId(1L)
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
        everySuspend { userRepository.getMyUserId() } returns UserId(1L)
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

        everySuspend { userRepository.getMyUserId() } returns myUserId
        every { blockRepository.createBlock(expectedCreateBlock) } returns flowOf(Result.Success(Unit))

        // when
        usecase(blockedId = blockedId, reasonId = reasonId, customReason = customReason).first()

        // then
        verify { blockRepository.createBlock(expectedCreateBlock) }
    }
}
