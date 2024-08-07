import com.android.build.api.dsl.ApplicationExtension
import io.photopixels.buildlogic.config.configureKotlinAndroid
import io.photopixels.buildlogic.config.installArtifactRenameTasks
import io.photopixels.buildlogic.config.installGitHooksTasks
import io.photopixels.buildlogic.extensions.findPluginId
import io.photopixels.buildlogic.extensions.findVersion
import io.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(findPluginId("androidApplication"))
                apply(findPluginId("kotlinAndroid"))
                apply(findPluginId("kspPlugin"))
                apply("plugin.detekt")
                apply("plugin.ktlint")
                apply("plugin.firebase")

                // Uncomment to enable flavors as defined in AppFlavors.kt
                // apply("plugin.flavors")
            }

            installGitHooksTasks()
            installArtifactRenameTasks()

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    targetSdk = findVersion("targetSdk").toInt()
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
                        manifestPlaceholders["ALLOW_HTTP_TRAFFIC"] = false
                        manifestPlaceholders["NETWORK_SECURITY_CONFIG"] = "@xml/network_config_https"
                    }

                    debug {
                        manifestPlaceholders["ALLOW_HTTP_TRAFFIC"] = true
                        manifestPlaceholders["NETWORK_SECURITY_CONFIG"] = "@xml/network_config_http"
                    }

                    create("releaseHttp") {
                        initWith(buildTypes["release"])

                        manifestPlaceholders["ALLOW_HTTP_TRAFFIC"] = true
                        manifestPlaceholders["NETWORK_SECURITY_CONFIG"] = "@xml/network_config_http"

                        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
