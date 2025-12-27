package com.peekr.core.presentation.feature.keywordGraph.util

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 클러스터 간 배치 형태
 */
enum class ClusterLayout {
    /** 방사형 */
    Radial,

    /** 나선형 */
    Spiral,

    /** 허니콤 */
    Honeycomb,

    /** 피보나치 */
    Fibonacci,

    /** 레이더 */
    Radar,
    ;

    fun getOffset(
        index: Int,
        spacing: Float,
        nodesPerLayer: Int = 1,
    ): Offset = when (this) {
        Radial -> calculateRadialOffset(index, spacing, nodesPerLayer)
        Spiral -> calculateSpiralOffset(index, spacing)
        Honeycomb -> calculateHoneycombOffset(index, spacing)
        Fibonacci -> calculateFibonacciOffset(index, spacing)
        Radar -> calculateRadarOffset(index, spacing, nodesPerLayer)
    }
}

// ------------------------------ 배치 형태 ------------------------------
// 사용자 수가 적으면 1번 방사형 추천
// 사용자 수가 많으면 4번 피보나치 추천
// 데이터 연결이 중요할 때 3번 허니콤 추천

// 1. 방사형
private fun calculateRadialOffset(
    index: Int,
    spacing: Float,
    nodesPerLayer: Int,
): Offset {
    if (index == 0) return Offset.Zero

    val angleStep = 360f / nodesPerLayer // 각도 간격 (72도)

    val layer = ((index - 1) / nodesPerLayer) + 1
    val positionInLayer = (index - 1) % nodesPerLayer

    val currentRadius = spacing * layer
    val angleDegree = (positionInLayer * angleStep) - 90f + (layer * 15f)
    val angleRadian = angleDegree * (Math.PI / 180f).toFloat()

    return Offset(
        x = (cos(angleRadian.toDouble()) * currentRadius).toFloat(),
        y = (sin(angleRadian.toDouble()) * currentRadius).toFloat(),
    )
}

// 2. 나선형
private fun calculateSpiralOffset(index: Int, spacing: Float = 700f): Offset {
    if (index == 0) return Offset.Zero // 첫 번째 유저는 정중앙

    // 황금각(Golden Angle)을 활용한 나선형 배치
    val goldenAngle = Math.PI * (3.0 - sqrt(5.0))
    val radius = spacing * sqrt(index.toDouble())
    val angle = index * goldenAngle

    val x = (radius * cos(angle)).toFloat()
    val y = (radius * sin(angle)).toFloat()

    return Offset(x, y)
}

// 3. 허니콤
private fun calculateHoneycombOffset(index: Int, spacing: Float): Offset {
    if (index == 0) return Offset.Zero

    // 육각형 링(layer) 계산
    var layer = 0
    var count = 0
    while (count < index) {
        layer++
        count += 6 * layer
    }

    val prevCount = count - 6 * layer
    val posInLayer = index - prevCount - 1
    val side = posInLayer / layer
    val posInSide = posInLayer % layer

    // 육각형의 각 꼭짓점 좌표 (60도 단위)
    val angle1 = (side * 60.0) * PI / 180.0
    val angle2 = ((side + 2) * 60.0) * PI / 180.0

    val corner1 = Offset((cos(angle1) * layer * spacing).toFloat(), (sin(angle1) * layer * spacing).toFloat())
    val corner2 = Offset((cos(angle2) * layer * spacing).toFloat(), (sin(angle2) * layer * spacing).toFloat())

    // 두 꼭짓점 사이를 선형 보간하여 변 위에 배치
    return corner1 + (corner2 - corner1) * (posInSide.toFloat() / layer)
}

// 4. 피보나치
private fun calculateFibonacciOffset(index: Int, spacing: Float): Offset {
    if (index == 0) return Offset.Zero

    // 황금각 (Golden Angle) 활용: 약 137.5도
    val goldenAngle = PI * (3.0 - sqrt(5.0))
    val radius = spacing * sqrt(index.toDouble())
    val angle = index * goldenAngle

    return Offset(
        x = (cos(angle) * radius).toFloat(),
        y = (sin(angle) * radius).toFloat(),
    )
}

// 5. 레이더
private fun calculateRadarOffset(
    index: Int,
    spacing: Float,
    nodesPerLayer: Int,
): Offset {
    if (index == 0) return Offset.Zero

    // 1. 깊이(Depth)와 해당 층에서의 순번 계산
    val depth = ((index - 1) / nodesPerLayer) + 1 // 1, 2, 3...
    val positionInLayer = (index - 1) % nodesPerLayer // 0, 1, 2, 3, 4

    // 2. 거리 계산 (안쪽 원부터 순차적으로 멀어짐)
    val radius = spacing * depth

    // 3. 각도 계산 (360도를 노드 개수로 나눔)
    val angleDegree = (positionInLayer * (360f / nodesPerLayer)) - 90f + (depth * 20f)
    val angleRadian = Math.toRadians(angleDegree.toDouble())

    return Offset(
        x = (cos(angleRadian) * radius).toFloat(),
        y = (sin(angleRadian) * radius).toFloat(),
    )
}
