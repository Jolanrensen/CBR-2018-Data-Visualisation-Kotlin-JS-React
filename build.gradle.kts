import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("js") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
}

apply {
    plugin("kotlin-dce-js")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    maven(url = "https://kotlin.bintray.com/js-externals")
    maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers")
    maven(url = "https://jitpack.io")
    maven(url = "https://dl.bintray.com/korlibs/korlibs")
    maven(url = "https://dl.bintray.com/soywiz/soywiz")
}

kotlin {
    target {
        useCommonJs()
        nodejs()
        browser {
            compilations.all {
                kotlinOptions {
                    metaInfo = true
                    sourceMap = true
                    sourceMapEmbedSources = "always"
                    moduleKind = "commonjs"
                    main = "call"
                    freeCompilerArgs = listOf("-Xinline-classes")
                }
            }
        }
    }

    sourceSets {
        main {
            dependencies {
                implementation(npm("webpack", "4.41.2"))
                implementation(npm("webpack-cli", "3.3.10"))
                implementation(npm("webpack-dev-server", "3.9.0"))

                implementation("org.jetbrains:annotations:16.0.2")
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.6.12")
                implementation("org.jetbrains:kotlin-react:16.9.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-react-dom:16.9.0-pre.89-kotlin-1.3.60")

                implementation("io.data2viz:d2v-data2viz-js:0.8.0-RC5")

                implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-css:1.0.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-css-js:1.0.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.11.1")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.1")

                implementation(npm("react", "16.12.0"))
                implementation(npm("react-dom", "16.12.0"))
                implementation(npm("react-draggable"))
                implementation(npm("react-list"))

                implementation(npm("inline-style-prefixer"))
                implementation(npm("core-js"))
                implementation(npm("@material-ui/core"))
                implementation(npm("@material-ui/icons"))

                implementation(npm("styled-components"))
                implementation(npm("jquery"))
                implementation(npm("object-copy"))
                implementation(npm("shebang-regex"))
            }
        }
    }
}
