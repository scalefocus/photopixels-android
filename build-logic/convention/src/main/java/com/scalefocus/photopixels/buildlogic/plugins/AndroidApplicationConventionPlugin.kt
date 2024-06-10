import com.android.build.api.dsl.ApplicationExtension
import com.scalefocus.photopixels.buildlogic.config.configureKotlinAndroid
import com.scalefocus.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.devtools.ksp")
                apply("plugin.detekt")
                apply("plugin.ktlint")

                // Uncomment to enable flavors as defined in AppFlavors.kt
                //apply("plugin.flavors")
            }

            installGitHooksTasks()
            installArtifactRenameTasks()

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                    versionCode = (properties["versionCode"] as String?)?.toIntOrNull() ?: 1
                    versionName = (properties["majorVersion"] as String? ?: "0") +
                        "." + (properties["minorVersion"] as String? ?: "0") +
                        "." + (properties["patchVersion"] as String? ?: "1-SNAPSHOT")

                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }

                buildFeatures {
                    buildConfig = true
                }

                buildTypes {
                    release {
                        isDebuggable = false
                        isMinifyEnabled = true
                        isShrinkResources = true
                        enableUnitTestCoverage = false
                    }
                }

                // Exclude these files from apk
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                        excludes += "/META-INF/INDEX.LIST"
                        excludes += "META-INF/DEPENDENCIES"
                        excludes += "META-INF/io.netty.versions.properties"
                    }
                }

                dependencies {
                    "implementation"(libs.findLibrary("timber").get())
                }
            }
        }
    }
}
