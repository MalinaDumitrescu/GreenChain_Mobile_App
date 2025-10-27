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
rootProject.name = "GreenChain"
include(
    ":app", ":core", ":feature"
//    ":core:model",
//    ":core:data",
//    ":core:network",
//    ":core:database",
//    ":feature:scan",
//    ":feature:leaderboard",
//    ":feature:social",
//    ":feature:quests",
//    ":feature:map",
//    ":feature:notifications"
)

include(
    ":core:model", ":core:data", ":core:network", ":core:database"
)
project(":core:model").projectDir = file("core/model")
project(":core:data").projectDir = file("core/data")
project(":core:network").projectDir = file("core/network")
project(":core:database").projectDir = file("core/database")

include(":feature:scan")
include(":feature:leaderboard")
include(":feature:social")
include(":feature:quests")
include(":feature:map")
include(":feature:notifications")
