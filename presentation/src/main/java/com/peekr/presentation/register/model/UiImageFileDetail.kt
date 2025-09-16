package com.peekr.presentation.register.model

import com.peekr.domain.account.model.ImageFileDetail
import com.peekr.domain.account.model.Mime
import com.peekr.presentation.common.file.image.FileNameGenerator

/**
 * UI용 이미지 파일 세부 사항
 *
 * @property bytes [ByteArray]타입의 이미지 파일
 * @property name 이미지 파일 이름
 * @property mime 이미지 파일 유형 (기본 값은 image/jpeg)
 */
data class UiImageFileDetail(
    private val _bytes: ByteArray,
    val name: String,
    val mime: Mime = Mime.IMAGE_JPEG,
) {
    // 방어적 복사로 불변성 보장
    val bytes: ByteArray
        get() = _bytes.copyOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UiImageFileDetail

        if (!_bytes.contentEquals(other._bytes)) return false
        if (name != other.name) return false
        if (mime != other.mime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _bytes.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + mime.hashCode()
        return result
    }

    override fun toString(): String =
        "UiImageFileDetail(name=$name, mime=$mime, bytesSize=${_bytes.size})"

    companion object {
        fun create(
            bytes: ByteArray,
            username: String,
            mime: Mime = Mime.IMAGE_JPEG,
        ): UiImageFileDetail {
            val fileName = FileNameGenerator.generate(username)
            return UiImageFileDetail(bytes, fileName, mime)
        }
    }
}

fun UiImageFileDetail.toDomainModel(): ImageFileDetail =
    ImageFileDetail(
        bytes,
        name = name,
        mime = mime,
    )
