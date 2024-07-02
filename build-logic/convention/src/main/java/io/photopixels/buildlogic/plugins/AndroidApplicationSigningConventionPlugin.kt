import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

class AndroidApplicationSigningConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val keystorePropsFile = rootProject.file("keystore.properties")
            val validSigningConfig = keystorePropsFile.exists()

            val keystoreProps = Properties().apply {
                if (validSigningConfig) {
                    load(keystorePropsFile.inputStream())
                } else {
                    println("Keystore file doesn't exist in [${keystorePropsFile.absolutePath}].")
                }
            }

            val keystorePath: String by keystoreProps
            val keystorePassword: String by keystoreProps
            val keyAlias: String by keystoreProps
            val keyPassword: String by keystoreProps

            extensions.configure<ApplicationExtension> {
                signingConfigs {
                    if (validSigningConfig) {
                        create("release") {
                            this.storeFile = file(keystorePath)
                            this.storePassword = keystorePassword
                            this.keyAlias = keyAlias
                            this.keyPassword = keyPassword
                        }
                    }
                }

                buildTypes {
                    release {
                        if (validSigningConfig) {
                            signingConfig = signingConfigs.getByName("release")
                        }
                    }
                    debug {
                        if (validSigningConfig) {
                            signingConfig = signingConfigs.getByName("release")
                        }
                    }
                }
            }
        }
    }
}
