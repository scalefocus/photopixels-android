package com.scalefocus.photopixels.buildlogic.config

import com.android.build.api.dsl.CommonExtension
import com.scalefocus.photopixels.buildlogic.extensions.libs
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val composeBom = libs.findLibrary("compose.bom").get()
            add("implementation", platform(composeBom))

            val composeBundle = libs.findBundle("compose").get()
            add("implementation", composeBundle)
        }

        val extension = extensions.getByType(ComposeCompilerGradlePluginExtension::class.java)
        buildComposeMetricsParameters(extension)
    }
}

@Suppress("MaxLineLength")
private fun Project.buildComposeMetricsParameters(extension: ComposeCompilerGradlePluginExtension) {
    with(extension) {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }

        fun Provider<*>.relativeToRootProject(dir: String) = flatMap {
            rootProject.layout.buildDirectory.dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        // enableStrongSkippingMode.set(true) // Improves runtime performance but is experimental for now
    }
}
