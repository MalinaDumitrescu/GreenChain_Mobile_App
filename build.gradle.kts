plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false

    // Quality gates at root added later
   // alias(libs.plugins.spotless)
    //alias(libs.plugins.detekt)
}

//spotless {
//    kotlin {
//        target("**/*.kt")
//        targetExclude("**/build/**")
//        ktlint(libs.versions.ktlint.get())
//        trimTrailingWhitespace()
//        endWithNewline()
//        indentWithSpaces()
//    }
//    kotlinGradle {
//        target("**/*.kts")
//        targetExclude("**/build/**", "**/.gradle/**")
//        ktlint(libs.versions.ktlint.get())
//    }
//}
//
//detekt {
//    buildUponDefaultConfig = true
//    allRules = false
//    autoCorrect = false
//    source.setFrom(files(projectDir))
//    config.setFrom(files("$rootDir/config/detekt/detekt.yml")) // create this file
//    // produce reports (handy in CI artifacts)
//    reports {
//        xml.required.set(true)
//        html.required.set(true)
//        txt.required.set(false)
//        sarif.required.set(false)
//    }
//}

//tasks.register("quality") {
    //group = "verification"
    //description = "Runs spotlessCheck and detekt for all modules"
    //dependsOn("spotlessCheck", "detekt")
//}
