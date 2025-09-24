package com.peekr.peekrapp

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


internal fun Project.configureAndroidLibrary(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        dependencies {
            // Core Android
            add("implementation", libs.findLibrary("androidx.core.ktx").get())
            add("implementation", libs.findLibrary("androidx.lifecycle.runtime.ktx").get())

            // Testing
            add("testImplementation", libs.findLibrary("junit").get())
            add("testImplementation", libs.findLibrary("androidx.test.core").get())
            add("androidTestImplementation", libs.findLibrary("androidx.test.runner").get())
        }
    }
}
