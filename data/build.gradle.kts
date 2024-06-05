import java.util.Properties

plugins {
    id("plugin.library")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
    id("plugin.networking")
}

android {
    namespace = "com.scalefocus.data"

    val properties = Properties().apply {
        with(rootProject.file("photopixels.properties")) { if (exists()) load(inputStream()) }
    }
    val webClientId = properties.getProperty("GOOGLE_OAUTH_WEB_CLIENT_ID") ?: "\"\""
    val webClientSecret = properties.getProperty("GOOGLE_OAUTH_WEB_CLIENT_SECRET") ?: "\"\""

    defaultConfig {
        buildConfigField("String", "GOOGLE_OAUTH_WEB_CLIENT_ID", webClientId)
        buildConfigField("String", "GOOGLE_OAUTH_WEB_CLIENT_SECRET", webClientSecret)
        consumerProguardFiles("proguard-rules.pro")
    }
}

dependencies {
    implementation(project(":domain"))

    // Security, DataStore
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.play.services.auth)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Google Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Google Photos
    implementation(libs.bundles.google.photos)
}
