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
include(":behavior")

include(":screenshort")

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

include(":permission")
project(":permission").projectDir = File("platform/permission")

include(":location")
project(":location").projectDir = File("platform/location")

include(":files")
project(":files").projectDir = File("platform/files")

project(":behavior").projectDir = File("platform/behavior")
project(":screenshort").projectDir = File("platform/screenshort")

include(":monitor")
project(":monitor").projectDir = File("platform/monitor")

//platform end

//the third start
include(":umeng")
project(":umeng").projectDir = File("third/umeng")

include(":pdf")
project(":pdf").projectDir = File("third/pdf")

include(":jiguang")
project(":jiguang").projectDir = File("third/jiguang")
//the third end

