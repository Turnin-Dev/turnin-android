package com.turnin.core.data.di

import com.turnin.core.data.source.network.ktor.createHttpClient
import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import com.turnin.core.domain.common.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class SharedDataModuleTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `플랫폼 모듈의 재료로 공지 리포지토리를 조립해 요청까지 수행한다`() = runTest {
        // given: 플랫폼 모듈이 SharedQualifier로 재료를 제공 (Android: SharedDataKoin, iOS: :shared)
        val engine = MockEngine {
            respond(
                content = """[{"id":1,"title":"공지","content":"내용","targetAudience":"ALL","isRead":false,"createdAt":0}]""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val koin = koinApplication {
            modules(
                module {
                    single<HttpClient>(SharedQualifier.AuthHttpClient) { createHttpClient(engine, "https://turnin.test") }
                    single<CoroutineDispatcher>(SharedQualifier.IoDispatcher) { UnconfinedTestDispatcher(testScheduler) }
                },
                sharedDataModule,
            )
        }.koin

        // when
        val result = koin.get<AnnouncementRepository>().getAnnouncements().last()

        // then
        assertTrue(result is Result.Success)
        assertEquals("공지", (result as Result.Success).data.single().title)
    }
}
