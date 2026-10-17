package com.turnin.shared.di

import com.turnin.shared.data.core.di.PlatformQualifier
import org.koin.core.module.Module

/**
 * 공유 기능 모듈 목록 (기능 추가 시 여기에 모듈을 추가한다)
 *
 * 플랫폼 재료([PlatformQualifier])를 등록한 모듈과 함께 시작해야 한다.
 * `single` 인스턴스는 [Module] 객체에 보관되므로 플랫폼마다 Koin 컨테이너를 하나만 사용하고,
 * 컨테이너를 다시 만들 때는 이전 컨테이너를 먼저 닫아야 한다. (테스트 포함)
 */
val sharedModules: List<Module> = listOf(
    announcementModule,
)
