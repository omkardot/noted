plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

}
buildscript {
    dependencies {
        // Add this to project level build.gradle
        classpath ("com.google.gms:google-services:4.4.0")

    }
    repositories {
        maven(url = "https://jitpack.io")
    }

}
