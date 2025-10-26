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
include(":app",
    ":core:model",
    ":core:data",
    ":core:network",
    ":core:database",
    ":feature:scan",
    ":feature:leaderboard",
    ":feature:social",
    ":feature:quests",
    ":feature:map",
    ":feature:notifications"
)
