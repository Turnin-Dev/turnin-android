package com.turnin.app.di

import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.junit.Assert.assertNotNull
import org.junit.Test

class SharedKoinBridgeTest {
    @Test
    fun `Hilt 재료로 공유 Koin 컨테이너를 조립해 공지 UseCase를 제공한다`() {
        // given
        val koin = SharedKoinBridge.provideSharedKoin(
            okHttpClient = OkHttpClient(),
            ioDispatcher = Dispatchers.Unconfined,
        )

        // when
        val useCases = try {
            SharedAnnouncementModule.provideAnnouncementUseCases(koin)
        } finally {
            // single 인스턴스가 Koin Module 객체에 남지 않도록 닫는다.
            koin.close()
        }

        // then
        assertNotNull(useCases)
        assertNotNull(useCases.getAnnouncements)
    }
}
