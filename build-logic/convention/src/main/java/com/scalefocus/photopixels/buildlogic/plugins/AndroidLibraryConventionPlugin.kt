import com.android.build.gradle.LibraryExtension
import com.scalefocus.photopixels.buildlogic.config.configureKotlinAndroid
import com.scalefocus.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.devtools.ksp")
                apply("plugin.detekt")
                apply("plugin.ktlint")

                // Uncomment to enable flavors as defined in AppFlavors.kt
                //apply("plugin.flavors")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    minSdk = libs.findVersion("minSdk").get().toString().toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                buildFeatures {
                    buildConfig = true
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        isShrinkResources = false
                        enableUnitTestCoverage = false
                    }
                }
            }

            dependencies {
                "implementation"(libs.findLibrary("lifecycle.runtime.ktx").get())
                "implementation"(libs.findLibrary("timber").get())
            }
        }
    }
}
