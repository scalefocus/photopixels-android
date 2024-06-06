plugins {
    id("plugin.library")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
}

android {
    namespace = "com.scalefocus.workers"

    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        manifestPlaceholders["appAuthRedirectScheme"] = "com.scalefocus.photopixels"
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":domain"))

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
}
