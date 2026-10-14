package com.turnin.core.domain.file

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FileNameGeneratorTest {
    @Test
    fun `generate - 사용자명-yyMMddHHmm-uuid8자리 형식으로 생성한다`() {
        val fileName = FileNameGenerator.generate("turnin")

        assertTrue(fileName, fileName.matches(Regex("""turnin-\d{10}-[0-9a-f]{8}""")))
    }

    @Test
    fun `generate - 호출할 때마다 다른 이름을 생성한다`() {
        assertNotEquals(FileNameGenerator.generate("turnin"), FileNameGenerator.generate("turnin"))
    }
}
