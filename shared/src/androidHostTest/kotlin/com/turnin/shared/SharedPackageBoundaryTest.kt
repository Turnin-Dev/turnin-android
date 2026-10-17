package com.turnin.shared

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * 공유 모듈의 패키지 경계 검사
 *
 * 계층 경계(domain ↔ data ↔ presentation)는 Gradle 모듈 의존성이 강제하지만,
 * 같은 모듈 안의 기능(`feature/<기능>`)·공통(`core`) 패키지 경계는 컴파일러가 막지 못하므로 이 테스트로 검사한다.
 * 이 경계를 지켜야 추후 기능 단위 모듈(`:shared:feature:<기능>:*`)로 나눌 때 폴더 이동만으로 분리할 수 있다.
 */
class SharedPackageBoundaryTest {
    private val sourceFiles: List<SourceFile> by lazy { loadSharedSourceFiles() }

    @Test
    fun `기능 패키지는 다른 기능 패키지를 import하지 않는다`() {
        // given
        val featureFiles = sourceFiles.filter { it.feature != null }

        // when
        val violations = featureFiles.flatMap { file ->
            file.importedFeatures()
                .filter { importedFeature -> importedFeature != file.feature }
                .map { importedFeature -> "${file.path} → feature.$importedFeature" }
        }

        // then
        assertTrue(featureFiles.isNotEmpty(), "검사할 기능 패키지 소스를 찾지 못했습니다.")
        assertTrue(
            violations.isEmpty(),
            "여러 기능이 함께 쓰는 코드는 core 패키지로 옮겨야 합니다.\n" + violations.joinToString("\n"),
        )
    }

    @Test
    fun `core 패키지는 기능 패키지를 import하지 않는다`() {
        // given
        val coreFiles = sourceFiles.filter { it.isCore }

        // when
        val violations = coreFiles.flatMap { file ->
            file.importedFeatures().map { importedFeature -> "${file.path} → feature.$importedFeature" }
        }

        // then
        assertTrue(coreFiles.isNotEmpty(), "검사할 core 패키지 소스를 찾지 못했습니다.")
        assertTrue(
            violations.isEmpty(),
            "core는 특정 기능에 의존하면 안 됩니다.\n" + violations.joinToString("\n"),
        )
    }

    private class SourceFile(
        val path: String,
        packageName: String,
        private val imports: List<String>,
    ) {
        /** 속한 기능 이름 (기능 패키지가 아니면 null) */
        val feature: String? = FEATURE_PACKAGE.find(packageName)?.groupValues?.get(1)

        val isCore: Boolean = CORE_PACKAGE.containsMatchIn(packageName)

        /** import한 기능 이름 목록 */
        fun importedFeatures(): Set<String> =
            imports.mapNotNull { FEATURE_PACKAGE.find(it)?.groupValues?.get(1) }.toSet()
    }

    private fun loadSharedSourceFiles(): List<SourceFile> {
        val sharedDir = File(findProjectRoot(), "shared")
        return sharedDir.walkTopDown()
            .onEnter { it.name != "build" }
            .filter { it.isFile && it.extension == "kt" && "${File.separator}src${File.separator}" in it.path }
            .map { file ->
                val lines = file.readLines()
                SourceFile(
                    path = file.relativeTo(sharedDir).path,
                    packageName = lines.firstOrNull { it.startsWith("package ") }?.removePrefix("package ")?.trim().orEmpty(),
                    imports = lines.filter { it.startsWith("import ") }.map { it.removePrefix("import ").trim() },
                )
            }
            .toList()
    }

    private fun findProjectRoot(): File =
        generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
            .firstOrNull { File(it, "settings.gradle.kts").exists() }
            ?: error("프로젝트 루트(settings.gradle.kts)를 찾을 수 없습니다.")

    private companion object {
        /** `com.turnin.shared.<계층>.feature.<기능>` */
        val FEATURE_PACKAGE = Regex("""^com\.turnin\.shared\.\w+\.feature\.(\w+)""")

        /** `com.turnin.shared.<계층>.core` */
        val CORE_PACKAGE = Regex("""^com\.turnin\.shared\.\w+\.core(\.|$)""")
    }
}
