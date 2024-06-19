package io.photopixels.buildlogic.config

import dev.android.buildlogic.util.OS
import dev.android.buildlogic.util.getOsName
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register

internal fun Project.installGitHooksTasks() {
    // Task to copy git hooks from "config/git/hooks" to ".git/hooks"
    tasks.register("copyGitHooks", CopyGitHooksTask::class) {
        description = "Install git hooks if missing or modified"
        group = "git"

        doFirst {
            print("Copying git hooks...")
        }

        inputDir.set(project.layout.projectDirectory.dir("$rootDir/config/git/hooks"))
        outputDir.set(project.layout.projectDirectory.dir("$rootDir/.git/hooks"))

        doLast {
            println(" done.")
        }
    }

    // Task to install git hooks
    tasks.register("installGitHooks", Exec::class) {
        description = "Install git hooks"
        group = "git"

        dependsOn(tasks["copyGitHooks"])

        doFirst {
            print("Installing git hooks...")
        }

        when (getOsName()) {
            OS.LINUX, OS.MAC, OS.OTHER -> {
                // Make git hooks scripts executable
                workingDir(rootDir)
                commandLine("chmod")
                setArgs(listOf("-R", "+x", ".git/hooks"))
            }

            else -> {
                // At least one command needs to be executed for the Exec task to succeed. "cmd" is the obvious
                // choice for builds done on Windows. Replace as needed.
                commandLine("cmd")
            }
        }

        doLast {
            println(" done.")
        }
    }

    afterEvaluate {
        tasks["clean"].dependsOn("installGitHooks")
        tasks["build"].dependsOn("installGitHooks")

        tasks.matching { task -> task.name.startsWith("assemble") || task.name.startsWith("bundle") }
            .configureEach { dependsOn("installGitHooks") }
    }
}

internal open class CopyGitHooksTask : DefaultTask() {

    @get:InputDirectory
    val inputDir: DirectoryProperty = project.objects.directoryProperty()

    @get:OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun copyFiles() {
        val inputDirFile = inputDir.get().asFile
        val outputDirFile = outputDir.get().asFile

        outputDirFile.mkdirs()
        inputDirFile.copyRecursively(target = outputDirFile, overwrite = true, onError = { file, ioException ->
            println("Error copying [$file], cause = $ioException")
            OnErrorAction.TERMINATE
        })
    }
}
