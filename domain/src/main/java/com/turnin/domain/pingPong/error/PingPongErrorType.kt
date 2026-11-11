package com.turnin.domain.pingPong.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.error.CommonErrorType

/**
 * 핑퐁 에러 타입
 */
sealed interface PingPongErrorType : BaseError {
    // ------------------------------ 질문 작성 ------------------------------

    /** 질문이 비어있거나 (`?`만 있는 경우 포함) 최대 글자 수를 초과한 경우 */
    data object InvalidQuestion : PingPongErrorType

    /** 본인 게시물에 질문하려는 경우 (PP002) */
    data object CannotQuestionOwnUserKeyword : PingPongErrorType

    /** 작성 간격 제한을 초과한 경우 (HTTP 429) */
    data object TooManyQuestions : PingPongErrorType

    // ------------------------------ 답변 작성 ------------------------------

    /** 답변이 비어있거나 최대 글자 수를 초과한 경우 */
    data object InvalidAnswer : PingPongErrorType

    /** 게시물 작성자가 아닌 사용자가 답변하려는 경우 (PP004) */
    data object NotUserKeywordOwner : PingPongErrorType

    /** 차단 관계인 질문자의 질문에 답변하려는 경우 (PP006) */
    data object CannotAnswerBlockedQuestioner : PingPongErrorType

    /** 이미 답변한 질문인 경우 (PP005) */
    data object AlreadyAnswered : PingPongErrorType

    // ------------------------------ 공통 (작성) ------------------------------

    /** 게시물이 삭제되었거나 볼 수 없는 경우 (PP001) */
    data object UserKeywordNotFound : PingPongErrorType

    /** 답변하려는 질문이 삭제되었거나 볼 수 없는 경우 (PP003) */
    data object QuestionNotFound : PingPongErrorType

    // ------------------------------ 삭제 ------------------------------

    /** 삭제하려는 질문이 이미 삭제되었거나 볼 수 없는 경우 (PP003) */
    data object QuestionAlreadyDeleted : PingPongErrorType

    /** 삭제하려는 답변이 이미 삭제되었거나 볼 수 없는 경우 (PP008) */
    data object AnswerAlreadyDeleted : PingPongErrorType

    /** 질문을 삭제할 권한이 없는 경우 (PP007) */
    data object NoPermissionToDeleteQuestion : PingPongErrorType

    /** 답변을 삭제할 권한이 없는 경우 (PP007) */
    data object NoPermissionToDeleteAnswer : PingPongErrorType

    /** 알 수 없는 에러로 자세한 사항은 [cause] 파라미터에 [Throwable]형태로 담는다. */
    data class Unexpected(val cause: Throwable?) : PingPongErrorType

    // ------------------------------ Other Error Type ------------------------------

    data class CommonError(val error: CommonErrorType) : PingPongErrorType
}
