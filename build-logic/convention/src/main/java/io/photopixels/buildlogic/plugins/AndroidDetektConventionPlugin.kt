import io.photopixels.buildlogic.extensions.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.DetektReports
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class AndroidDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            allprojects {
                with(pluginManager) {
                    val detektPlugin = libs.findPlugin("detektPlugin").get().get().pluginId
                    apply(detektPlugin)
                }

                val extension = extensions.getByType<DetektExtension>()
                configureDetekt(extension)

                val javaVersionString = libs.findVersion("javaVersion").get().toString()
                val javaVersion = JavaVersion.valueOf(javaVersionString).toString()

                tasks.withType<Detekt>().configureEach {
                    jvmTarget = javaVersion
                    reports = configureDetektReports(objects)
                }

                tasks.withType<DetektCreateBaselineTask>().configureEach {
                    jvmTarget = javaVersion
                }
            }
        }
    }
}

private fun Project.configureDetekt(extension: DetektExtension) = extension.apply {
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = false
    config.setFrom("$rootDir/config/detekt/detekt-config.yml")

    // TODO There are many requests for consolidated baseline files. Let's monitor this so one day we can have
    //  something like: file("$rootDir/config/detekt/detekt-baseline.xml"). For the moment, we are consolidating
    //  baseline files from all modules into a single directory with the name of the module appended to the file name.
    baseline = file("$rootDir/config/detekt/detekt-baseline-${project.name}.xml")

    dependencies {
        // "detektPlugins"(libs.findLibrary("detekt-formatting").get())
        "detektPlugins"(libs.findLibrary("detekt-cli").get())
        "detektPlugins"(libs.findLibrary("detekt-compose").get())
        "detektPlugins"(libs.findLibrary("detekt-rules-compose").get())
    }
}

private fun configureDetektReports(objects: ObjectFactory): DetektReports {
    return DetektReports(objects).apply {
        // XML: checkstyle-like format mainly for integrations like Jenkins
        xml.required.set(true)

        // HTML: observe findings in your browser with structure and code snippets
        html.required.set(true)

        // TXT: similar to the console output, contains issue signature to manually edit baseline files
        txt.required.set(true)

        // SARIF: standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations
        // with GitHub Code Scanning
        sarif.required.set(true)

        // MD: simple Markdown format
        md.required.set(true)
    }
}
