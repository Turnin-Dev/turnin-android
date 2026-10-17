package com.turnin.shared.di

import com.turnin.shared.data.core.di.SharedQualifier
import com.turnin.shared.data.core.network.createHttpClient
import com.turnin.shared.domain.core.result.Result
import com.turnin.shared.domain.feature.announcement.usecase.AnnouncementUseCases
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

@OptIn(ExperimentalCoroutinesApi::class)
class SharedModulesTest {
    @Test
    fun `플랫폼 모듈의 재료로 공지 UseCase를 조립해 요청까지 수행한다`() = runTest {
        // given: 플랫폼 모듈이 SharedQualifier로 재료를 제공 (Android: Koin 브릿지, iOS: SharedEntryPoint)
        val engine = MockEngine {
            respond(
                content = """[{"id":1,"title":"공지","content":"내용","targetAudience":"ALL","isRead":false,"createdAt":0}]""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        // single 인스턴스는 Module 객체에 보관되므로 테스트가 끝나면 반드시 닫아 다른 테스트(SharedEntryPointTest 등)에 남지 않게 한다.
        val koinApplication = koinApplication {
            modules(
                listOf(
                    module {
                        single<HttpClient>(SharedQualifier.AuthHttpClient) {
                            createHttpClient(engine, "https://turnin.test")
                        }
                        single<CoroutineDispatcher>(SharedQualifier.IoDispatcher) {
                            UnconfinedTestDispatcher(testScheduler)
                        }
                    },
                ) + sharedModules,
            )
        }

        // when
        val result = try {
            koinApplication.koin.get<AnnouncementUseCases>().getAnnouncements().last()
        } finally {
            koinApplication.close()
        }

        // then
        assertTrue(result is Result.Success)
        assertEquals("공지", result.data.single().title)
    }
}
