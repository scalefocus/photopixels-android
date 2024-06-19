plugins {
    id("plugin.library")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
}

android {
    namespace = "io.photopixels.domain"

    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        manifestPlaceholders["appAuthRedirectScheme"] = "io.photopixels.app"
    }
}

dependencies {
}
