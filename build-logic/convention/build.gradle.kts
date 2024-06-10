plugins {
    `kotlin-dsl`
}

group = "com.scalefocus.photopixels.buildlogic"

java {
    sourceCompatibility = JavaVersion.valueOf(libs.versions.javaVersion.get())
    targetCompatibility = JavaVersion.valueOf(libs.versions.javaVersion.get())
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
    compileOnly(libs.ktlint.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "plugin.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "plugin.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("composeApp") {
            id = "plugin.compose.application"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("compose") {
            id = "plugin.compose.library"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        register("androidHilt") {
            id = "plugin.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        register("test") {
            id = "plugin.test"
            implementationClass = "TestingConventionPlugin"
        }

        register("androidTest") {
            id = "plugin.test.android"
            implementationClass = "AndroidTestingConventionPlugin"
        }

        register("jvm") {
            id = "plugin.jvm"
            implementationClass = "JvmConventionPlugin"
        }

        register("flavors") {
            id = "plugin.flavors"
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }

        register("networking") {
            id = "plugin.networking"
            implementationClass = "NetworkingConventionPlugin"
        }

        register("androidDetekt") {
            id = "plugin.detekt"
            implementationClass = "AndroidDetektConventionPlugin"
        }

        register("androidKtlint") {
            id = "plugin.ktlint"
            implementationClass = "AndroidKtlintConventionPlugin"
        }

        register("applicationSigning") {
            id = "plugin.application.signing"
            implementationClass = "AndroidApplicationSigningConventionPlugin"
        }
    }
}
