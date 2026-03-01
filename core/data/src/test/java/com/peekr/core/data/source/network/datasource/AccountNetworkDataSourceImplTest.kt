package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.ServerTestRule
import com.peekr.core.data.source.network.api.AccountApi
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountNetworkDataSourceImplTest {
    @get:Rule
    val testRule = ServerTestRule()

    private val accountApi: AccountApi
        get() = testRule.createNetworkApi<AccountApi>(testRule.moshi)

    private lateinit var dataSource: AccountNetworkDataSource

    @Before
    fun setUp() {
        dataSource = AccountNetworkDataSourceImpl(accountApi)
    }

    @Test
    fun `계정 삭제 - 성공 테스트`() = runTest {
        // given
        testRule.server.enqueue(
            MockResponse().apply {
                setResponseCode(200)
            },
        )

        // when
        val response = dataSource.deleteAccount()

        // then
        assertTrue(response is NetworkResult.Success)
    }

    @Test
    fun `계정 삭제 - 알 수 없는 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val mockApi: AccountApi = mockk()
        val exception = Exception()
        dataSource = AccountNetworkDataSourceImpl(mockApi)
        coEvery { mockApi.deleteAccount() } throws exception

        // when
        val response = dataSource.deleteAccount()

        // then
        val errorResponse = response as NetworkResult.Error
        assertEquals(NetworkErrorType.Unexpected(exception), errorResponse.error)
    }
}
