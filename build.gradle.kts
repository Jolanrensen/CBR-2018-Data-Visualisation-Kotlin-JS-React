plugins {
    id("org.jetbrains.kotlin.js") version "1.3.61"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://kotlin.bintray.com/js-externals")
    maven(url = "https://dl.bintray.com/kotlinx/kotlinx")
    maven(url = "https://dl.bintray.com/kotlin/kotlin-js-wrappers")
}

kotlin {
    target {
        browser {
            val main by compilations.getting {
                kotlinOptions {
                    sourceMap = true
                    moduleKind = "commonjs"
                    sourceMapEmbedSources = "always"
                }
            }
        }

        sourceSets["main"].dependencies {
            implementation(project(":kotlin-vue"))
            implementation(npm("core-js", "latest"))
            implementation(npm("vue", "latest"))
            implementation(npm("vue-router", "latest"))
            implementation(npm("@vue/composition-api", "latest"))
            implementation(npm("@fortawesome/fontawesome-svg-core", "latest"))
            implementation(npm("@fortawesome/free-solid-svg-icons", "latest"))
            implementation(npm("@fortawesome/vue-fontawesome", "latest"))
            implementation(kotlin("stdlib-js"))
            implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.6.12")
            implementation("io.data2viz:d2v-data2viz-js:0.8.0-RC1")
            implementation(npm("jquery"))
            implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.88-kotlin-1.3.60")
            implementation("org.jetbrains:kotlin-css:1.0.0-pre.88-kotlin-1.3.60")
            implementation("org.jetbrains:kotlin-css-js:1.0.0-pre.88-kotlin-1.3.60")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.11.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.1")
//    this one sucks    implementation("kotlin.js.externals:kotlin-js-jquery:3.2.0-0")
        }
    }
}