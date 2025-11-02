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
    ":app",
    ":core:model",
    ":core:data",
    ":core:network",
    ":core:database",
    ":feature:scan",
    ":feature:leaderboard",
    ":feature:social",
    ":feature:quests",
    ":feature:map",
    ":feature:notifications",
    ":feature:auth"
)

project(":core:model").projectDir = file("core/model")
project(":core:data").projectDir = file("core/data")
project(":core:network").projectDir = file("core/network")
project(":core:database").projectDir = file("core/database")
