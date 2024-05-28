import java.util.Properties

plugins {
    id("plugin.library")
    id("plugin.compose.library")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
    id("plugin.test.android")
}

android {
    namespace = "com.scalefocus.presentation"

    buildTypes {
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    val properties = Properties().apply {
        with(rootProject.file("photopixels.properties")) { if (exists()) load(inputStream()) }
    }
    val androidClientId = properties.getProperty("GOOGLE_OAUTH_ANDROID_CLIENT_ID") ?: "\"\""

    defaultConfig {
        buildConfigField("String", "GOOGLE_OAUTH_ANDROID_CLIENT_ID", androidClientId)
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.coil.compose)
    implementation(libs.glide)
    implementation(libs.glide.compose)
    ksp(libs.glide.compiler)
    ksp(libs.glide.ksp)

    // App auth - library
    implementation(libs.appauth)

    // JWT decoder
    implementation(libs.jwtdecode)
}
