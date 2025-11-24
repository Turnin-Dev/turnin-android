package com.peekr.core.domain.coroutine

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.combineWithResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private data class TestUser(val name: String)

private data class TestPost(val title: String)

private data class TestUserPost(val user: TestUser, val post: TestPost)

class CombineWithResultKtTest {
    @Test
    fun `두 Flow 모두 Success일 때 transform 함수가 적용된다`() = runTest {
        // given
        val flow1 = flowOf(Result.Success(1))
        val flow2 = flowOf(Result.Success(2))

        // when
        val result = combineWithResult(flow1, flow2) { f1, f2 ->
            Result.Success(f1.data + f2.data)
        }.toList()

        // then
        assertEquals(1, result.size)
        assertTrue(result.first() is Result.Success)
        assertEquals(3, (result.first() as Result.Success).data)
    }

    @Test
    fun `첫 번째 Flow가 Loading이면 Loading을 반환한다`() = runTest {
        // given
        val flow1: Flow<Result<Int, BaseError>> = flowOf(Result.Loading)
        val flow2: Flow<Result<Int, BaseError>> = flowOf(Result.Success(2))

        // when
        val result = combineWithResult(flow1, flow2) { f1, f2 ->
            Result.Success(f1.data + f2.data)
        }.toList()

        // then
        assertEquals(1, result.size)
        assertTrue(result.first() is Result.Loading)
    }

    @Test
    fun `두 번째 Flow가 Loading이면 Loading을 반환한다`() = runTest {
        // given
        val flow1: Flow<Result<Int, BaseError>> = flowOf(Result.Success(2))
        val flow2: Flow<Result<Int, BaseError>> = flowOf(Result.Loading)

        // when
        val result = combineWithResult(flow1, flow2) { f1, f2 ->
            Result.Success(f1.data + f2.data)
        }.toList()

        // then
        assertEquals(1, result.size)
        assertTrue(result.first() is Result.Loading)
    }

    @Test
    fun `두 Flow 모두 Loading이면 Loading을 반환한다`() = runTest {
        // given
        val flow1: Flow<Result<Int, BaseError>> = flowOf(Result.Loading)
        val flow2: Flow<Result<Int, BaseError>> = flowOf(Result.Loading)

        // when
        val result = combineWithResult(flow1, flow2) { f1, f2 ->
            Result.Success(f1.data + f2.data)
        }.toList()

        // then
        assertEquals(1, result.size)
        assertTrue(result.first() is Result.Loading)
    }

    @Test
    fun `첫 번째 Flow가 Error면 해당 Error를 반환한다`() = runTest {
        // given
        val error = CommonErrorType.Network.ClientError
        val flow1: Flow<Result<Int, BaseError>> = flowOf(Result.Error(error))
        val flow2: Flow<Result<Int, BaseError>> = flowOf(Result.Success(1))

        // when
        val result = combineWithResult(flow1, flow2) { f1, f2 ->
            Result.Success(f1.data + f2.data)
        }.toList()

        // then
        assertEquals(1, result.size)
        assertTrue(result.first() is Result.Error)
        assertEquals(error, (result.first() as Result.Error).error)
    }

    @Test
    fun `두 번째 Flow가 Error면 해당 Error를 반환한다`() = runTest {
        // given
        val error = CommonErrorType.Network.ClientError
        val flow1: Flow<Result<Int, BaseError>> = flowOf(Result.Success(1))
        val flow2: Flow<Result<Int, BaseError>> = flowOf(Result.Error(error))

        // when
        val result = combineWithResult(flow1, flow2) { f1, f2 ->
            Result.Success(f1.data + f2.data)
        }.toList()

        // then
        assertEquals(1, result.size)
        assertTrue(result.first() is Result.Error)
        assertEquals(error, (result.first() as Result.Error).error)
    }

    @Test
    fun `두 Flow 모두 Error일 때 첫 번째 Error를 반환한다`() = runTest {
        // given
        val error1 = CommonErrorType.Network.ClientError
        val error2 = CommonErrorType.Network.ServerError
        val flow1: Flow<Result<Int, BaseError>> = flowOf(Result.Error(error1))
        val flow2: Flow<Result<Int, BaseError>> = flowOf(Result.Error(error2))

        // when
        val result = combineWithResult(flow1, flow2) { f1, f2 ->
            Result.Success(f1.data + f2.data)
        }.toList()

        // then
        assertEquals(1, result.size)
        assertTrue(result.first() is Result.Error)
        assertEquals(error1, (result.first() as Result.Error).error)
    }

    @Test
    fun `Loading후 Success를 방출 시 정상적으로 처리된다`() = runTest {
        // given
        val flow1: Flow<Result<Int, BaseError>> = flowOf(Result.Loading, Result.Success(1))
        val flow2: Flow<Result<Int, BaseError>> = flowOf(Result.Success(2))

        // when
        val result = combineWithResult(flow1, flow2) { f1, f2 ->
            Result.Success(f1.data + f2.data)
        }.toList()

        // then
        assertEquals(2, result.size)
        assertTrue(result[0] is Result.Loading)
        assertTrue(result[1] is Result.Success)
        assertEquals(3, (result[1] as Result.Success).data)
    }

    @Test
    fun `복잡한 데이터도 정상적으로 처리한다`() = runTest {
        // given
        val user = TestUser("ksj")
        val post = TestPost("hello")
        val userFlow = flowOf(Result.Success(user))
        val postFlow = flowOf(Result.Success(post))

        // when
        val result = combineWithResult(userFlow, postFlow) { userF, postF ->
            val userPost = TestUserPost(userF.data, postF.data)
            Result.Success(userPost)
        }.toList()

        // then
        assertEquals(1, result.size)
        assertTrue(result[0] is Result.Success)
        val userPost = (result[0] as Result.Success).data
        assertEquals(user, userPost.user)
        assertEquals(post, userPost.post)
    }
}
