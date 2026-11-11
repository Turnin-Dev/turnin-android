package com.turnin.core.domain.common.error

/**
 * 외부 시스템(Ex. 서버)과 계약된 에러 코드
 *
 * 명세서와 바로 대조할 수 있도록 enum 이름을 서버 에러 코드 그대로 사용한다. (의미는 각 항목의 KDoc 참고)
 * 의미 있는 이름은 기능별 에러 타입(`XxxErrorType`)으로 변환할 때 부여한다.
 */
sealed interface ContractErrorCode {
    data object Unexpected : ContractErrorCode

    /** 인증 에러 코드 */
    enum class Auth : ContractErrorCode {
        /** 중복된 사용자 */
        A002,
    }

    /**
     * 핑퐁 에러 코드
     *
     * 같은 HTTP 상태코드(403, 404)가 여러 상황을 뜻하므로 상황은 이 코드로 구분한다.
     */
    enum class PingPong : ContractErrorCode {
        /** 게시물(사용자 키워드)을 조회할 수 없는 경우 (존재하지 않음, 비활성화, 차단 관계) */
        PP001,

        /** 본인 게시물에 질문을 등록하려는 경우 */
        PP002,

        /** 핑퐁이 없거나 질문이 숨김 처리된 경우 */
        PP003,

        /** 게시물 작성자가 아닌 사용자가 답변을 등록하려는 경우 */
        PP004,

        /** 이미 답변이 등록된 질문에 답변을 등록하려는 경우 */
        PP005,

        /** 차단 관계(양방향)인 질문자의 질문에 답변을 등록하려는 경우 */
        PP006,

        /** 삭제 권한이 없는 경우 */
        PP007,

        /** 답변이 없거나 숨김 처리된 경우 */
        PP008,
    }
}
