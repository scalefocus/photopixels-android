import com.scalefocus.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class AndroidKtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            allprojects {
                with(pluginManager) {
                    val ktlintPlugin = libs.findPlugin("ktlintPlugin").get().get().pluginId
                    apply(ktlintPlugin)
                }

                val extension = extensions.getByType<KtlintExtension>()
                configureKtlint(extension)
            }

            // Default heap size for KtLint Gradle workers is 256 MB.
            tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
                workerMaxHeapSize.set("256m")
            }
        }
    }
}

private fun Project.configureKtlint(extension: KtlintExtension) = extension.apply {
    version.set(libs.findVersion("ktlint").get().toString())
    debug.set(true)
    verbose.set(true)
    android.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    enableExperimentalRules.set(false)
    relative.set(false)
    baseline.set(file("ktlint-baseline.xml"))

    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.PLAIN_GROUP_BY_FILE)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.HTML)
    }

    kotlinScriptAdditionalPaths {
        include(fileTree("scripts/"))
    }

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}
