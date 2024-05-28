package com.scalefocus.presentation.utils

import android.content.Context

object Utils {
    fun getVersionName(context: Context): String {
        val packageName = context.packageName
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    }
}
