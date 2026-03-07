import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinCompose) // <--- Compose Compiler plugin'i eklendi
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
    // Ekstra ikonlar için material-icons-extended ekledik
    implementation(compose.materialIconsExtended)
    implementation("com.google.firebase:firebase-admin:9.2.0")
    
    // SLF4J uyarısını gidermek için basit bir logger
    implementation("org.slf4j:slf4j-simple:2.0.7")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "AtlasTerminal"
            packageVersion = "1.0.0"
            description = "Atlas Yazılım Plaka Takip Sistemi PC Terminali"
            copyright = "© 2026 Atlas Yazılım. Tüm hakları saklıdır."
            vendor = "Atlas_yazılım"
            
            windows {
                menu = true
                shortcut = true
            }
        }
    }
}
