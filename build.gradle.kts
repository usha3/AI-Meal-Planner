plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    // Only if you really need google-services classpath, otherwise not needed
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath ("com.google.gms:google-services:4.4.0")
    }
}

// Remove allprojects { repositories {…} } completely