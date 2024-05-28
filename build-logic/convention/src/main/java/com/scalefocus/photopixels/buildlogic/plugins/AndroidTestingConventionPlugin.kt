import com.scalefocus.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidTestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                val composeBom = libs.findLibrary("compose.bom").get()
                add("androidTestImplementation", platform(composeBom))

                "androidTestImplementation"(libs.findLibrary("androidx.test.ext.junit").get())
                "androidTestImplementation"(libs.findLibrary("espresso.core").get())
                "androidTestImplementation"(libs.findLibrary("ui.test.manifest").get())
                "androidTestImplementation"(libs.findLibrary("ui.test.junit4").get())
            }
        }
    }
}
