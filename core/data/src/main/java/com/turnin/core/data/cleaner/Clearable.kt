package com.turnin.core.data.cleaner

/**
 * 로그아웃 또는 계정 탈퇴 시 데이터 정리를 수행하는 구성 요소에 대한 인터페이스.
 * * 이 인터페이스를 구현하고 Hilt 멀티바인딩(@IntoSet)으로 등록하면,
 * [AppDataCleaner]에서 일괄적으로 호출되어 로컬 데이터를 초기화한다.
 */
fun interface Clearable {
    /**
     * 저장 중인 모든 데이터를 삭제하거나 초기 상태로 되돌린다.
     * 네트워크 통신 없이 순수 로컬 데이터(DB, Cache, Prefs) 정리 로직만 포함해야 한다.
     */
    suspend fun clear()
}
