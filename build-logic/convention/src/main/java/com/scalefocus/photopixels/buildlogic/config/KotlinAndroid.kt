package com.scalefocus.photopixels.buildlogic.config

import com.android.build.api.dsl.CommonExtension
import com.scalefocus.photopixels.buildlogic.extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

        defaultConfig {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()
        }

        compileOptions {
            val javaVersion = libs.findVersion("javaVersion").get().toString()
            sourceCompatibility = JavaVersion.valueOf(javaVersion)
            targetCompatibility = JavaVersion.valueOf(javaVersion)
        }

        configureKotlin()
    }
}

private fun Project.configureKotlin() {
    // todo: Use withType as workaround for https://youtrack.jetbrains.com/issue/KT-55947
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            val javaVersion = libs.findVersion("javaVersion").get().toString()
            jvmTarget = JavaVersion.valueOf(javaVersion).toString()

            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()

            freeCompilerArgs = freeCompilerArgs +
                listOf(
                    // Enable experimental coroutines APIs
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                )
        }
    }
}
