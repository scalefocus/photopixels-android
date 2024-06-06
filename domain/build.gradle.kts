plugins {
    id("plugin.library")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
}

android {
    namespace = "com.scalefocus.domain"

    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
        manifestPlaceholders["appAuthRedirectScheme"] = "com.scalefocus.photopixels"
    }
}

dependencies {
}
