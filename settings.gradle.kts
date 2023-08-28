pluginManagement {
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

rootProject.name = "fanji"
include(":app")
include(":resource")
include(":ui")
include(":util")
include(":img")
include(":net")
include(":okhttp")
include(":thread")
include(":play")
include(":cache")
include(":third")

//platform start
project(":util").projectDir = File("platform/util")
project(":cache").projectDir = File("platform/cache")
project(":play").projectDir = File("platform/play")
project(":okhttp").projectDir = File("platform/okhttp")
project(":net").projectDir = File("platform/net")
project(":img").projectDir = File("platform/img")
project(":ui").projectDir = File("platform/ui")
project(":thread").projectDir = File("platform/thread")
project(":third").projectDir = File("platform/third")

//platform end
