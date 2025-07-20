package com.peekr.domain.shared.dataStore

import kotlinx.coroutines.flow.Flow

/**
 * DataStore Manager 클래스
 *
 * DataStore Preference를 통해 다양한 타입의 데이터를 저장하고 조회할 수 있는 기능을 제공한다.
 *
 * 현재 지원하는 타입
 * 1. `String`
 * 2. `Boolean`
 *
 * @see DataStoreKey DataStore 키 값 집합
 */
interface DataStoreManager {
    /**
     * DataStore 에서 키 값을 통해 String 타입의 데이터를 저장하거나 수정한다.
     *
     * @param key DataStore 키
     * @param value 저장할 데이터
     * @exception WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun saveStringData(key: DataStoreKey, value: String)

    /**
     * DataStore 에서 키 값을 통해 Boolean 타입의 데이터를 저장하거나 수정한다.
     *
     * @param key DataStore 키
     * @param value 저장할 데이터
     * @exception WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun saveBooleanData(key: DataStoreKey, value: Boolean)

    /**
     * DataStore 에서 키 값을 통해 String 타입의 데이터를 가져온다.
     *
     * @param key DataStore 키
     * @return Flow<String>, 데이터가 없다면 null
     */
    fun getStringData(key: DataStoreKey): Flow<String?>

    /**
     * DataStore 에서 키 값을 통해 Boolean 타입의 데이터를 가져온다.
     *
     * @param key DataStore 키
     * @return Flow<Boolean>, 데이터가 없다면 null
     */
    fun getBooleanData(key: DataStoreKey): Flow<Boolean?>

    /**
     * DataStore 에서 키 값을 통해 String 타입의 데이터를 삭제한다.
     *
     * @param key DataStore 키
     * @exception WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun deleteStringData(key: DataStoreKey)

    /**
     * DataStore 에서 키 값을 통해 Boolean 타입의 데이터를 삭제한다.
     *
     * @param key DataStore 키
     * @exception WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun deleteBooleanData(key: DataStoreKey)

    /**
     * DataStore 데이터를 전부 삭제한다.
     *
     * @exception WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     * @throws Exception 데이터 삭제 중 오류가 발생할 때
     */
    suspend fun clearAll()
}
