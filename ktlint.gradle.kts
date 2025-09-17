// 1. KtLint 설정을 위한 configuration 생성
val ktlint by configurations.creating

// 2. ktlint 의존성 추가
dependencies {
    ktlint("com.pinterest.ktlint:ktlint-cli:1.4.1") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
    ktlint("io.nlopez.compose.rules:ktlint:0.4.22") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

// 3. 스타일 검사 Task
tasks.register<JavaExec>("ktlintCheck") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
        // "!:some-module-name/**", // <- ! exclude specific module from formatting
    )
}

// 4. 자동 포맷 Task
tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style and format"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args(
        "-F",
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
        // "!:some-module-name/**", // <- ! exclude specific module from formatting
    )
}

// 5. pre-commit hook 자동 설치 Task
tasks.register<Copy>("installKtlintGitHookToPreCommit") {
    val preCommitContent =
        """
        #!/bin/bash
        STAGED_FILES=${'$'}${'$'}(git diff --name-only --cached --relative -- '*.kt' '*.kts')
        if [ -n "${'$'}STAGED_FILES" ]; then
          echo "${'$'}STAGED_FILES" | xargs ktlint --relative
          if [ ${'$'}? -ne 0 ]; then
            exit 1
          fi
        fi
        """.trimIndent()

    val preCommitFile = File(rootProject.rootDir, ".git/hooks/pre-commit")
    preCommitFile.writeText(preCommitContent)
    preCommitFile.setExecutable(true)
}
