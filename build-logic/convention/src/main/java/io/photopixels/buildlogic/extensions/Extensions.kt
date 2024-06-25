package io.photopixels.buildlogic.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/** This function retrieves the plugin ID a given pre-defined plugin from the version catalog. */
fun Project.findPluginId(pluginName: String): String = libs
    .findPlugin(pluginName)
    .get()
    .get()
    .pluginId

/** This function retrieves the version string of a given pre-defined item from the version catalog. */
fun Project.findVersion(alias: String) = libs
    .findVersion(alias)
    .get()
    .toString()

fun String.toCamelCase(): String =
    split("_").joinToString("") { newString ->
        newString.lowercase().replaceFirstChar { it.titlecase() }
    }

var Project.buildType: String?
    get() {
        return null
    }
    set(value) {}
