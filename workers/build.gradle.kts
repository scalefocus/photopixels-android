plugins {
    id("plugin.library")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
}

android {
    namespace = "io.photopixels.workers"

    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        manifestPlaceholders["appAuthRedirectScheme"] = "io.photopixels.app"
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":domain"))

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
}
