package io.photopixels.buildlogic.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.findPluginId(pluginName: String): String = libs
    .findPlugin(pluginName)
    .get()
    .get()
    .pluginId

fun Project.findVersion(alias: String) = libs
    .findVersion(alias)
    .get()
    .toString()

fun String.toCamelCase(): String =
    split("_").joinToString("") { newString ->
        newString.lowercase().replaceFirstChar { it.titlecase() }
    }
