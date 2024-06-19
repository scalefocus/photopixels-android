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

    // FIXME https://github.com/firebase/firebase-android-sdk/issues/3148
    //  This is a known issue with the Crashlytics Extension not being properly registered from a Gradle
    //  convention plugin. There is a dedicated function in AndroidFirebaseConventionPlugin.kt that can be used when
    //  this issue is resolved.
    firebaseCrashlytics {
        mappingFileUploadEnabled = true
        nativeSymbolUploadEnabled = true
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
