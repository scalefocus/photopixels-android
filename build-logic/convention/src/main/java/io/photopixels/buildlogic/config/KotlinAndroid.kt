package io.photopixels.buildlogic.config

import com.android.build.api.dsl.CommonExtension
import io.photopixels.buildlogic.extensions.findVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = findVersion("compileSdk").toInt()

        defaultConfig {
            minSdk = findVersion("minSdk").toInt()
        }

        compileOptions {
            val javaVersion = findVersion("javaVersion")
            sourceCompatibility = JavaVersion.valueOf(javaVersion)
            targetCompatibility = JavaVersion.valueOf(javaVersion)
        }

        val extension = extensions.getByType<KotlinAndroidProjectExtension>()
        configureKotlin(extension)
    }
}

private fun Project.configureKotlin(extension: KotlinAndroidProjectExtension) {
    with(extension) {
        compilerOptions {
            val javaVersionName = findVersion("javaVersion")
            val javaVersion = JavaVersion.valueOf(javaVersionName).majorVersion

            jvmTarget.set(JvmTarget.fromTarget(javaVersion))

            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: Boolean? by project
            allWarningsAsErrors.set(warningsAsErrors)

            freeCompilerArgs.set(
                listOf(
                    // Enable experimental coroutines APIs
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                )
            )
        }
    }
}
