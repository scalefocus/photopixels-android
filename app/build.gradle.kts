plugins {
    id("plugin.application")
    id("plugin.application.signing")
    id("plugin.compose.application")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
    id("plugin.test.android")
}

android {
    namespace = "io.photopixels.app"

    defaultConfig {
        applicationId = "io.photopixels.app"

        manifestPlaceholders["appAuthRedirectScheme"] = "io.photopixels.app"
    }

    buildTypes {
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":workers"))

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Play services
    implementation(libs.play.services.auth)
}
