plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.greenchain.feature.scan"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        // Read from gradle properties / local.properties
        val apiKey = (project.findProperty("ROBOFLOW_API_KEY") as String? ?: "").ifBlank { "dummy_api_key" }
        val modelId = (project.findProperty("ROBOFLOW_MODEL_ID") as String? ?: "").ifBlank { "dummy_model_id" }

        buildConfigField("String", "ROBOFLOW_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "ROBOFLOW_MODEL_ID", "\"$modelId\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets")
        }
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":feature:profile"))

    // Guava
    implementation("com.google.guava:guava:32.0.1-android")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation("org.tensorflow:tensorflow-lite:2.14.0")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // 🔌 Networking for RoboFlow (from your version catalog)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi.core)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
}
