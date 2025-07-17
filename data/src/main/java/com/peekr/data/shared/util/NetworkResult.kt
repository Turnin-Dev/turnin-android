package com.peekr.data.shared.util

import com.peekr.data.shared.util.network.NetworkErrorType

/** 네트워크 호출 결과 래퍼 클래스 */
sealed interface NetworkResult<out T> {
    /**
     * 네트워크 호출 성공 시
     *
     * @property data 성공 후 반환할 데이터
     */
    data class Success<out T>(val data: T) : NetworkResult<T>

    /**
     * 네트워크 호출 실패 시
     *
     * @property error 에러 타입
     * @property code 에러 코드
     * @property status 에러 상태 코드
     * @property message 에러 메시지
     */
    data class Error(
        val error: NetworkErrorType,
        val code: String? = null,
        val status: Int? = null,
        val message: String? = null,
    ) : NetworkResult<Nothing>
}
