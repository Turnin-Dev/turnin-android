package com.peekr.core.data.source.network.util

import com.peekr.core.data.source.network.error.NetworkErrorType

/**
 * 네트워크 호출 결과 래퍼 클래스
 *
 * `적용 범위`: 네트워크 호출 하는 모든 로직 (NetworkCall, NetworkDataSource 등)
 */
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
     * @property error 네트워크 에러 타입
     * @property code 서버 에러 코드
     * @property status HTTP 상태 코드
     * @property message 서버 에러 메시지 (이 메시지가 직접적으로 사용자에게 노출되면 안된다.)
     */
    data class Error(
        val error: NetworkErrorType,
        val code: String? = null,
        val status: Int? = null,
        val message: String? = null,
    ) : NetworkResult<Nothing>
}
