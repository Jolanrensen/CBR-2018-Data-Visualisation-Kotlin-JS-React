plugins {
    id("org.jetbrains.kotlin.js") version "1.3.61"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    target.browser { }

    sourceSets["main"].dependencies {
        implementation(kotlin("stdlib-js"))
        implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.6.12")
        implementation("io.data2viz:d2v-data2viz-js:0.8.0-RC1")
    }
}