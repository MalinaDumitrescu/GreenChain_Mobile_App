plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)

}

android {
    namespace = "com.greenchain.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.greenchain.app"
        minSdk = 24
        targetSdk = 34          // it can be 35
        versionCode = 1
        versionName = "0.1.0"

        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // handy flags for debug logging, etc.
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    //kps(libs.room.compiler)


    // Compose BOM first
    implementation(platform(libs.compose.bom))

    // AndroidX + Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.firebase.crashlytics)
    debugImplementation(libs.compose.ui.tooling)

    // Google Maps (if needed)
    implementation(libs.play.services.maps)

    // Hilt (KSP)
    implementation(libs.hilt.android)   // <-- CORRECT accessor
    ksp(libs.hilt.compiler)             // <-- CORRECT accessor


    // ---- Firebase (use BOM to pin all versions) ----
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ktx)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // Projects (moved out of the wrong nested dependencies block)
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    implementation(project(":feature:leaderboard"))
    implementation(project(":feature:map"))
    implementation(project(":feature:notifications"))
    implementation(project(":feature:quests"))
    implementation(project(":feature:scan"))
    implementation(project(":feature:social"))
    implementation (project(":feature:auth"))
}
