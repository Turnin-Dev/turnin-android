package com.peekr.core.data.source.local.datastore

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
    // ------------------------------ 일반 저장 & 읽기 메서드 ------------------------------

    /**
     * DataStore 에서 키 값을 통해 String 타입의 데이터를 저장하거나 수정한다.
     *
     * @param key DataStore 키
     * @param value 저장할 데이터
     * @exception com.peekr.core.data.source.local.error.WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun saveStringData(key: DataStoreKey, value: String)

    /**
     * DataStore 에서 키 값을 통해 Boolean 타입의 데이터를 저장하거나 수정한다.
     *
     * @param key DataStore 키
     * @param value 저장할 데이터
     * @exception com.peekr.core.data.source.local.error.WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun saveBooleanData(key: DataStoreKey, value: Boolean)

    /**
     * DataStore 에서 키 값을 통해 Long 타입의 데이터를 저장하거나 수정한다.
     *
     * @param key DataStore 키
     * @param value 저장할 데이터
     * @exception com.peekr.core.data.source.local.error.WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun saveLongData(key: DataStoreKey, value: Long)

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
     * DataStore 에서 키 값을 통해 Long 타입의 데이터를 가져온다.
     *
     * @param key DataStore 키
     * @return Flow<Long>, 데이터가 없다면 null
     */
    fun getLongData(key: DataStoreKey): Flow<Long?>

    // ------------------------------ 암호화 저장 & 읽기 메서드 ------------------------------

    /**
     * DataStore 에서 키 값을 통해 암호화된 String 타입의 데이터를 저장하거나 수정한다.
     *
     * @param key DataStore 키
     * @param value 저장할 데이터
     * @exception com.peekr.core.data.source.local.error.WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun saveEncryptedStringData(key: DataStoreKey, value: String)

    /**
     * DataStore 에서 키 값을 통해 암호화된 String 타입의 데이터를 가져온다.
     *
     * @param key DataStore 키
     * @return Flow<String>, 데이터가 없거나 예외가 발생하면 null을 반환한다.
     */
    fun getEncryptedStringData(key: DataStoreKey): Flow<String?>

    // ------------------------------ 삭제 메서드 ------------------------------

    /**
     * DataStore 에서 키 값을 통해 String 타입의 데이터를 삭제한다.
     *
     * 다른 타입의 값이 들어있는 키를 이용해 삭제하더라도 오류가 발생하지는 않는다. (내부적으로 Map 처럼 작동하기 때문에)
     *
     * @param key DataStore 키
     * @exception com.peekr.core.data.source.local.error.WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun deleteStringData(key: DataStoreKey)

    /**
     * DataStore 에서 키 값을 통해 Boolean 타입의 데이터를 삭제한다.
     *
     * 다른 타입의 값이 들어있는 키를 이용해 삭제하더라도 오류가 발생하지는 않는다. (내부적으로 Map 처럼 작동하기 때문에)
     *
     * @param key DataStore 키
     * @exception com.peekr.core.data.source.local.error.WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun deleteBooleanData(key: DataStoreKey)

    /**
     * DataStore 에서 키 값을 통해 Long 타입의 데이터를 삭제한다.
     *
     * 다른 타입의 값이 들어있는 키를 이용해 삭제하더라도 오류가 발생하지는 않는다. (내부적으로 Map 처럼 작동하기 때문에)
     *
     * @param key DataStore 키
     * @exception com.peekr.core.data.source.local.error.WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     */
    suspend fun deleteLongData(key: DataStoreKey)

    /**
     * DataStore 데이터를 전부 삭제한다.
     *
     * @exception com.peekr.core.data.source.local.error.WritingDataException 데이터를 디스크에 쓸 때 발생할 수 있는 예외
     * @throws Exception 데이터 삭제 중 오류가 발생할 때
     */
    suspend fun clearAll()

    // ------------------------------ 유틸 ------------------------------

    /**
     * DataStore 내 데이터가 비어있는지 확인한다.
     */
    suspend fun isCleared(): Boolean
}
