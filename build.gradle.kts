// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kspPlugin) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.hiltAndroidPlugin) apply false
    alias(libs.plugins.detektPlugin) apply false
    alias(libs.plugins.ktlintPlugin) apply false
    alias(libs.plugins.kotlinComposeCompiler) apply false
    alias(libs.plugins.gmsGoogleServices) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    alias(libs.plugins.firebasePerformance) apply false
    alias(libs.plugins.firebaseAppDistribution) apply false
}
