@file:Suppress("ktlint:standard:filename")

package dev.android.buildlogic.util

internal enum class OS { WINDOWS, LINUX, MAC, OTHER }

internal fun getOsName(): OS {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("windows") -> OS.WINDOWS

        osName.contains("linux") -> OS.LINUX

        osName.contains("mac") -> OS.MAC

        else -> OS.OTHER
    }
}
