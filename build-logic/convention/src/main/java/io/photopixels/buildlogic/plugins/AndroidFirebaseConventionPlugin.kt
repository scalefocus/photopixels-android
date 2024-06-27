import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.FilterConfiguration
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.google.firebase.appdistribution.gradle.AppDistributionExtension
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import io.photopixels.buildlogic.config.buildNewVariantOutputName
import io.photopixels.buildlogic.extensions.findPluginId
import io.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import java.util.Locale

private const val BUILD_TYPE_DEBUG = "debug"
private const val BUILD_TYPE_RELEASE = "release"

class AndroidFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val firebaseConfigExists = file("google-services.json").exists()

            with(pluginManager) {
                if (firebaseConfigExists) {
                    apply(findPluginId("gmsGoogleServices"))
                    apply(findPluginId("firebaseCrashlytics"))
                    apply(findPluginId("firebasePerformance"))
                    apply(findPluginId("firebaseAppDistribution"))
                } else {
                    println("Firebase services gradle plugins are not enabled; missing google-services.json file.")
                    println("Searched in [${file("google-services.json").absolutePath}]")
                }
            }

            dependencies {
                "implementation"(platform(libs.findLibrary("firebase-bom").get()))

                "implementation"(libs.findBundle("firebase").get()) {
                    // Exclude dependencies that have conflicts with Google Photos API in the project
                    exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                    exclude(group = "com.google.firebase", module = "protolite-well-known-types")
                }
            }

            if (firebaseConfigExists) {
                configureFirebaseAppDistribution()
                configureFirebaseCrashlytics()
            }
        }
    }
}

/**
 * Configure Firebase App Distribution.
 * NOTE: If app flavors exist, this method will have to be modified to support them.
 */
private fun Project.configureFirebaseAppDistribution() {
    val buildTypesValue = project.findProperty("PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_BUILD_TYPES")?.toString()
        ?: gradleLocalProperties(rootDir, providers).getProperty("PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_BUILD_VARIANTS")

    if (buildTypesValue == null) {
        println("Error with Firebase Distribution. Missing buildTypesValue, check project properties")
        return
    }

    val buildTypes = buildTypesValue.split(",").map { it.trim() }

    val credentialsFile = project.findProperty("PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_CREDENTIALS_FILE")?.toString()
        ?: gradleLocalProperties(rootDir, providers)
            .getProperty("PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_CREDENTIALS_FILE")

    val artifactTypeValue = project.findProperty("PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_ARTIFACT_TYPE")?.toString()
        ?: gradleLocalProperties(rootDir, providers)
            .getProperty("PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_ARTIFACT_TYPE")

    val groupsValue = project.findProperty("PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_GROUPS")?.toString()
        ?: gradleLocalProperties(rootDir, providers)
            .getProperty("PHOTOPIXELS_FIREBASE_APPDISTRIBUTION_GROUPS")

    // For detailed information, see https://firebase.google.com/docs/app-distribution/android/distribute-gradle
    firebaseAppDistribution {
        if (buildTypes.contains(BUILD_TYPE_DEBUG)) Unit

        if (buildTypes.contains(BUILD_TYPE_RELEASE)) {
            prepareReleaseAppDistribution(
                appDistributionExtension = this,
                credentialsFile = credentialsFile,
                artifactTypeValue = artifactTypeValue,
                groupsValue = groupsValue,
                buildType = BUILD_TYPE_RELEASE
            )
        }
    }
}

private fun Project.prepareReleaseAppDistribution(
    appDistributionExtension: AppDistributionExtension,
    credentialsFile: String,
    artifactTypeValue: String,
    groupsValue: String,
    buildType: String
) {
    appDistributionExtension.apply {
        serviceCredentialsFile = credentialsFile
        artifactType = artifactTypeValue
        groups = groupsValue

        // TODO add release notes
        // releaseNotes = "Release notes"
        // releaseNotesFile = "/path/to/releasenotes.txt"

        // Because the output file name is not constant, but depends on variant properties and CI-set version number,
        // the block of code below takes care of setting the correct value to the "artifactPath" property.
        extensions.configure<AndroidComponentsExtension<*, *, *>>("androidComponents") {
            onVariants { variant ->
                if (variant !is ApplicationVariant) return@onVariants
                if (variant.buildType != buildType) return@onVariants

                val variantName = variant.name.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString()
                }

                variant.outputs
                    .filterIsInstance<com.android.build.api.variant.impl.VariantOutputImpl>()
                    .forEach { output ->
                        // Filter is null for universal APKs.
                        if (output.getFilter(FilterConfiguration.FilterType.ABI) == null) {
                            tasks
                                .filter { it.name.startsWith("appDistributionUpload$variantName") }
                                .onEach {
                                    val name = buildNewVariantOutputName(variant)
                                    val extension = artifactTypeValue.lowercase()
                                    val directory = "${layout.buildDirectory.get()}/outputs/$extension/$buildType"

                                    // Set firebaseAppDistribution.artifactPath to build output APK file name
                                    artifactPath = "$directory/$name.$extension"
                                }
                        }
                    }
            }
        }
    }
}

// FIXME See comment in "<rootDir>/app/build.gradle.kts"
private fun Project.configureFirebaseCrashlytics() {
//    extensions.configure<CrashlyticsExtension> {
//        mappingFileUploadEnabled = true
//        nativeSymbolUploadEnabled = true
//    }
}
