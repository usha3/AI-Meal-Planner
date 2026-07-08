plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mealplannerapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mealplannerapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    // ---------------- CORE ----------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ---------------- ANDROID UI ----------------
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.12.0")

    // ---------------- FIREBASE (CLEAN) ----------------
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")

    // ---------------- GOOGLE AUTH ----------------
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // ---------------- IMAGE LOADING ----------------
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // ---------------- NETWORK ----------------
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ---------------- JSON ----------------
    implementation("com.google.code.gson:gson:2.10.1")

    // ---------------- ROOM DATABASE ----------------
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // ---------------- LIFECYCLE ----------------
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")

    // ---------------- ML KIT ----------------
    implementation("com.google.mlkit:image-labeling:17.0.8")

    // ---------------- TEST ----------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("androidx.paging:paging-runtime:3.3.2")

    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")

    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.google.firebase:firebase-firestore:25.1.1")
    implementation ("com.google.firebase:firebase-storage:21.0.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.google.android.material:material:1.12.0")

    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
}