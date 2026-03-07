plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinCompose) // <--- Bu satır eklendi
}

kotlin {
    androidTarget()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
            }
        }
        val androidMain by getting
        val desktopMain by getting
    }
}

android {
    namespace = "com.sezgin.plaka_bilgisi.common"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
