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
# debugging stack traces.
# 스택 트레이스 줄 번호 유지 (Crashlytics 분석용)
-keepattributes SourceFile,LineNumberTable

# Kotlinx Serialization
-keep @kotlinx.serialization.Serializable class ** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** { *; }

# 제네릭, 어노테이션, 내부 클래스 정보 유지
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod

# Enum
-keep enum com.peekr.** { *; }

# Crashlytics
-keep public class * extends java.lang.Exception

# 카카오 SDK
-keep class com.kakao.sdk.**.model.* { <fields>; }

# Moshi
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class *JsonAdapter { *; }
