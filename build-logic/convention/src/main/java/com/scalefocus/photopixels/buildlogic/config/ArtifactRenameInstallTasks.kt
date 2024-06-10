import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import java.io.File

internal fun Project.installArtifactRenameTasks() {
    extensions.configure<AndroidComponentsExtension<*, *, *>>("androidComponents") {
        onVariants { variant ->
            if (variant !is ApplicationVariant) return@onVariants

            val variantName = variant.name
            val outputDir = file("${layout.buildDirectory.get()}/outputs/bundle/$variantName")

            val deleteAabTask = tasks.findByName("deleteAab") ?: tasks.register("deleteAab") {
                doLast {
                    outputDir.listFiles()
                        ?.filter { it.extension == "aab" }
                        ?.forEach { it.delete() }
                }
            }

            val newName = generateNewVariantOutputName(variant)

            // This block is good for APK files only
            variant.outputs.forEach { output ->
                if (output is com.android.build.api.variant.impl.VariantOutputImpl) {
                    output.outputFileName.set("$newName.apk")
                }
            }

            // This block is good for AAB files only
            tasks.matching {
                it.name.startsWith("bundle")
                    && !it.name.contains("Classes")
                    && !it.name.contains("Resources")
            }.configureEach {
                dependsOn(deleteAabTask)

                doLast {
                    outputDir.listFiles()
                        ?.firstOrNull { it.extension == "aab" }
                        ?.copyTo(File(outputDir, "$newName.aab"), true)
                        // We use copy (above) since "renameTo" breaks the "bundleXxx" task (AGP 8.4.1, Gradle 8.8)
                        //?.renameTo(File(outputDir, "$newName.aab"))
                }
            }
        }
    }
}

private fun Project.generateNewVariantOutputName(variant: ApplicationVariant): String {
    val androidExtension = project.extensions.getByType<BaseAppModuleExtension>()

    val appId = variant.applicationId.get()
    val versionName = androidExtension.defaultConfig.versionName ?: "unknown"
    val versionCode = androidExtension.defaultConfig.versionCode
    val buildType = variant.buildType ?: "unknown"
    //val flavorName = variant.flavorName.orEmpty()
    val flavorNameExpanded = variant.productFlavors.joinToString("-") { it.second }

    val newNameSuffix = buildString {
        append("-v$versionName($versionCode)")
        if (flavorNameExpanded.isNotEmpty()) append("-$flavorNameExpanded")
        append("-$buildType")
    }

    return "$appId$newNameSuffix"
}
