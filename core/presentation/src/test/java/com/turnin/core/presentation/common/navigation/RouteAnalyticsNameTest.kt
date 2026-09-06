package com.turnin.core.presentation.common.navigation

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteAnalyticsNameTest {
    private val analyticsNamePattern = Regex("^[a-z][a-z0-9_]*$")

    // sealed 계층을 재귀적으로 훑어 leaf(concrete) Route를 전부 수집
    private fun collectLeafRoutes(kClass: KClass<out Route>): List<KClass<out Route>> {
        val children = kClass.sealedSubclasses
        return if (children.isEmpty()) {
            listOf(kClass)
        } else {
            children.flatMap { collectLeafRoutes(it) }
        }
    }

    // 각 leaf 클래스에서 analyticsName 값을 실제로 얻어내기
    // - data object -> INSTANCE(혹은 objectInstance)에서 바로 읽음
    // - data class -> 기본 생성자를 리플렉션으로 호출해 임시 인스턴스 생성 후 읽음
    private fun KClass<out Route>.instantiateForTest(): Route {
        objectInstance?.let { return it }

        val constructor = primaryConstructor
            ?: error("기본 생성자가 없는 Route: $qualifiedName")

        val args = constructor.parameters.associateWith { param ->
            val classifier = param.type.classifier as? KClass<*>
            when {
                classifier == Long::class -> 0L
                classifier == Int::class -> 0
                classifier == Boolean::class -> false
                classifier == String::class -> ""
                classifier?.java?.isEnum == true ->
                    classifier.java.enumConstants?.first()
                param.type.isMarkedNullable -> null
                else -> error(
                    "테스트 더미 값 생성 규칙이 없는 타입: ${param.type} (Route: $qualifiedName)",
                )
            }
        }
        return constructor.callBy(args)
    }

    private val allRoutes: List<Route> by lazy {
        collectLeafRoutes(Route::class).map { it.instantiateForTest() }
    }

    @Test
    fun `analyticsName은 소문자와 언더스코어만 사용한다`() {
        allRoutes.forEach { route ->
            assertTrue(
                "잘못된 형식: ${route::class.simpleName} -> ${route.analyticsName}",
                analyticsNamePattern.matches(route.analyticsName),
            )
        }
    }

    @Test
    fun `analyticsName은 중복되지 않는다`() {
        val duplicates = allRoutes
            .groupBy { it.analyticsName }
            .filter { it.value.size > 1 }

        assertTrue("중복된 analyticsName: $duplicates", duplicates.isEmpty())
    }
}
