pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Photo Pixels"
include(":app")
include(":presentation")
include(":data")
include(":domain")
include(":workers")

//https://github.com/gradle/gradle/issues/28407?source=post_page-----6bc363ac1eb8--------------------------------
//There is an gradle issue about building project with that task
//TODO: Fix this when issue is resolved from gradle team
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))
