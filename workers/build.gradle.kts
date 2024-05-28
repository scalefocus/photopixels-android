plugins {
    id("plugin.library")
    id("plugin.jvm")
    id("plugin.hilt")
    id("plugin.test")
}

android {
    namespace = "com.scalefocus.workers"

    buildTypes {
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":domain"))

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
}
