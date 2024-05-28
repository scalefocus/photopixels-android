package com.scalefocus.photopixels.buildlogic.config

import com.android.build.api.dsl.CommonExtension
import com.scalefocus.photopixels.buildlogic.extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("composeCompiler").get().toString()
        }

        dependencies {
            val composeBom = libs.findLibrary("compose.bom").get()
            add("implementation", platform(composeBom))

            val composeBundle = libs.findBundle("compose").get()
            add("implementation", composeBundle)
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs += buildComposeMetricsParameters()
        }
    }
}

@Suppress("MaxLineLength")
private fun Project.buildComposeMetricsParameters(): List<String> {
    val metricParameters = mutableListOf<String>()

    val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
    val enableMetrics = (enableMetricsProvider.orNull == "true")
    if (enableMetrics) {
        val metricsFolder = File(project.layout.buildDirectory.asFile.get(), "compose-metrics")
        metricParameters.add("-P")
        metricParameters.add("plugin:androidx.compose.compiler.plugins.java:metricsDestination=${metricsFolder.absolutePath}")
    }

    val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
    val enableReports = (enableReportsProvider.orNull == "true")
    if (enableReports) {
        val reportsFolder = File(project.layout.buildDirectory.asFile.get(), "compose-reports")
        metricParameters.add("-P")
        metricParameters.add("plugin:androidx.compose.compiler.plugins.java:reportsDestination=${reportsFolder.absolutePath}")
    }

    return metricParameters.toList()
}
