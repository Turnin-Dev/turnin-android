package com.peekr.core.presentation.viewmodel

import com.peekr.core.domain.validation.ValidationErrorType
import com.peekr.core.domain.validation.ValidationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private typealias TextFieldValue = String

/**
 * 텍스트 필드 값 유효성 검사기
 *
 * 텍스트 필드 값 혹은 UI 상태 값에서 텍스트 필드 값을 추출하여
 * 해당 값에 대해 유효성 검사기([validator])를 설정할 수 있다.
 *
 * [value]의 반환 값(텍스트 필드 값)의 변화가 생길 때마다 Flow가 실행되며 빈 칸이 아닐 때에만 실행된다.
 *
 * 그리고 이 함수는 [ValidationResult]를 기반으로 하기 때문에
 * 유효성 검사기는 반드시 [ValidationResult]를 반환해야 한다.
 *
 * @param scope 실행될 코루틴 스코프 (보통 뷰모델에서 실행되므로 `viewModelScope`)
 * @param debounceMillis 디바운스 타임아웃 밀리초 (기본 값은 0)
 * @param value [T]에서 텍스트 필드 값을 반환
 * @param validator 유효성 검사기, 텍스트 필드 값인 파라미터를 기반으로 유효성 검사를 수행한 후
 * [ValidationResult]를 반환해야 한다.
 * @param onLoading 유효성 검사 로딩 시 (기본적으로 `null`)
 * @param onValid 유효성 검사 성공 시
 * @param onInvalid 유효성 검사 실패 시
 */
@OptIn(FlowPreview::class)
fun <T> StateFlow<T>.setTextFieldValidation(
    scope: CoroutineScope,
    debounceMillis: Long = 0,
    value: (T) -> TextFieldValue,
    validator: (TextFieldValue) -> ValidationResult<TextFieldValue, ValidationErrorType>,
    onLoading: (() -> Unit)? = null,
    onValid: (TextFieldValue) -> Unit,
    onInvalid: (ValidationErrorType) -> Unit,
): Job = this
    .map { value(it) }
    .distinctUntilChanged()
    .debounce(debounceMillis)
    .filter { it.isNotBlank() }
    .onEach {
        when (val result = validator(it)) {
            ValidationResult.Loading -> onLoading?.invoke()
            is ValidationResult.Valid -> onValid(result.value)
            is ValidationResult.Invalid -> onInvalid(result.error)
        }
    }.launchIn(scope)
