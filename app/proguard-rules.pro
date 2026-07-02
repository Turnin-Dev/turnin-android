# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# Uncomment this to preserve the line number information for

# ------------------------------ 추가 설정 ------------------------------
# 써드파티 및 라이브러리들은 자동으로 AAR에 포함되는 룰 제외 (중복이 있을 수도 있음)

# 스택 트레이스 줄 번호 유지 (Crashlytics 분석용)
-keepattributes SourceFile,LineNumberTable

# 제네릭, 어노테이션, 내부 클래스 정보 유지
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod

# Enum
-keep enum com.turnin.** { *; }

# Kotlinx Serialization
-keep @kotlinx.serialization.Serializable class ** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** { *; }

# Crashlytics
-keep public class * extends java.lang.Exception

# 카카오 SDK
-keep class com.kakao.sdk.**.model.* { <fields>; }
-dontwarn com.kakao.**

# Moshi
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class *JsonAdapter { *; }
-keep class com.squareup.moshi.** { *; }

# Retrofit & OkHttp
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Room
-keep @androidx.room.Entity class * { *; }

# Hilt
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# HTTP 캐시 커스텀 어노테이션 (리플렉션을 사용하므로)
-keep @interface *Cacheable
-keepclassmembers class * {
    @*Cacheable <methods>;
}

# Compose @Preview (제거 보장)
-checkdiscard class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}
-keepclassmembers,allowshrinking class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}
