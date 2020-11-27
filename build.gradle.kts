import org.jetbrains.compose.compose

plugins {
    kotlin("jvm") version "1.4.20"
    id("org.jetbrains.compose") version "0.2.0-build131"
}

repositories {
    jcenter()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}