package com.turnin.core.domain.common.error

import org.junit.Assert.assertEquals
import org.junit.Test

class ContractErrorCodeMapperTest {
    /**
     * 조회표에 그룹 등록을 빠뜨리거나 코드 값이 그룹 간에 중복되면 실패한다.
     *
     * [ContractErrorCode] 안에 선언된 모든 enum 그룹을 리플렉션으로 찾으므로, 그룹을 추가할 때 이 테스트를 고칠 필요가 없다.
     * 단, 그룹을 [ContractErrorCode] 밖(다른 파일)으로 옮기면 찾지 못하므로 이 테스트도 함께 수정해야 한다.
     */
    @Test
    fun `계약 에러 코드 - 모든 그룹의 코드가 조회표에 등록되어 자기 자신으로 변환된다`() {
        // given
        val allCodes: List<Enum<*>> = ContractErrorCode::class.java.declaredClasses
            .filter { it.isEnum }
            .flatMap { group -> group.enumConstants.map { it as Enum<*> } }

        // when
        val unmappedCodes = allCodes.filter { code -> code.name.toServerErrorCode() != code }

        // then
        assertEquals(emptyList<Enum<*>>(), unmappedCodes)
    }

    @Test
    fun `핑퐁 에러 코드 - 앞뒤 공백과 대소문자를 무시하고 변환한다`() {
        // given
        val rawCode = " pp005 "

        // when
        val actual = rawCode.toServerErrorCode()

        // then
        assertEquals(ContractErrorCode.PingPong.PP005, actual)
    }

    @Test
    fun `인증 에러 코드 - A002는 기존과 같이 변환한다`() {
        // given
        val rawCode = "A002"

        // when
        val actual = rawCode.toServerErrorCode()

        // then
        assertEquals(ContractErrorCode.Auth.A002, actual)
    }

    @Test
    fun `알 수 없는 코드 - Unexpected로 변환한다`() {
        // given
        val rawCode = "PP999"

        // when
        val actual = rawCode.toServerErrorCode()

        // then
        assertEquals(ContractErrorCode.Unexpected, actual)
    }
}
