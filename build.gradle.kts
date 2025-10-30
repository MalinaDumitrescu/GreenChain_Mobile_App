// Root build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
}

// ✅ Se aplică DOAR modulelor (nu root-ului)
subprojects {

    // 1) Elimină vechile adnotări IntelliJ care îți dublează clasele
    configurations.configureEach {
        exclude(group = "com.intellij", module = "annotations")
    }

    // 2) Adaugă o versiune unică pentru JetBrains annotations
    //    doar în modulele Android (acolo există 'implementation')
    plugins.withId("com.android.library") {
        dependencies {
            add("implementation", "org.jetbrains:annotations:24.1.0")
        }
    }
    plugins.withId("com.android.application") {
        dependencies {
            add("implementation", "org.jetbrains:annotations:24.1.0")
        }
    }
}
