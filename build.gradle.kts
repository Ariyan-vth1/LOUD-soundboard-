plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.loud.soundboard"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.loud.soundboard"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
