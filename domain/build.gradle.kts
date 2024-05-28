plugins {
    id("plugin.library")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
}

android {
    namespace = "com.scalefocus.domain"

    buildTypes {
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
}
