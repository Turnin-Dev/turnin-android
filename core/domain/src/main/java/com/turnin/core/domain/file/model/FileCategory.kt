package com.turnin.core.domain.file.model

/**
 * 파일 카테고리
 *
 * 파일명과 조합되어 사용되고 스토리지 서버에 [prefix]기반으로 하위 폴더명을 결정한다.
 */
enum class FileCategory(val prefix: String) {
    PROFILE_IMAGE("profile_images"),
}
