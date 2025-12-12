package com.peekr.core.data.paging

/**
 * 페이지네이션 API 응답 클래스에서 구현해야 하는 인터페이스.
 * 리스트 데이터를 담고 있어야 한다.
 *
 * @param hasNext 다음 항목 존재여부
 * @param list 리스트 항목의 데이터 모델 타입
 */
interface PagingDataHolder<T : Any> {
    val hasNext: Boolean
    val list: List<T>
}
