package com.peekr.core.common.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * 앱 버전 정보 제공자 클래스
 *
 * @param context [Context]
 */
class AppVersionProvider(private val context: Context) {
    private val packageInfo by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
    }

    /**
     * 버전 이름 (Ex. 0.0.1)
     */
    val versionName: String
        get() = packageInfo.versionName ?: "Unknown"

    /**
     * 버전 코드 (Ex. 1)
     */
    val versionCode: Long
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
}
