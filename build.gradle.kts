import org.jetbrains.compose.compose

plugins {
    kotlin("jvm") version "1.4.20"
    id("org.jetbrains.compose") version "0.3.0-build133"
}

repositories {
    jcenter()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.google.code.gson:gson:2.8.6")
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}