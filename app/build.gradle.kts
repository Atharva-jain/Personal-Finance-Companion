plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    id("androidx.room")
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.teapink.waste_samaritan.personalfinancecomapnion"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.teapink.waste_samaritan.personalfinancecomapnion"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }



}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Jetpack Compose integration
    implementation(libs.androidx.navigation.compose)
    // JSON serialization library, works with the Kotlin serialization plugin
    implementation(libs.kotlinx.serialization.json)

    // Koin for Android
    implementation(libs.koin.android)


    //coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // room database
    implementation(libs.androidx.room.runtime)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)
    annotationProcessor(libs.androidx.room.compiler)
    // optional - Test helpers
    testImplementation(libs.androidx.room.testing)
    // THIS IS THE MISSING PIECE:
    ksp(libs.androidx.room.compiler.v261)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.material)  // or latest version
    //icons
    implementation(libs.androidx.material.icons.extended)

    // Bio metric authentication
    implementation(libs.androidx.biometric)


}