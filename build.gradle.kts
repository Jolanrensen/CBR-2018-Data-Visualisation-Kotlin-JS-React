import org.gradle.kotlin.dsl.*

plugins {
    kotlin("js") version "1.3.61"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    maven(url = "https://kotlin.bintray.com/js-externals")
    maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers")
}

kotlin {
    target {
        useCommonJs()
//        nodejs()
        browser {
            compilations.all {
                kotlinOptions {
                    metaInfo = true
                    outputFile = "${project.buildDir.path}/js/${project.name}.js"
                    sourceMap = true
                    sourceMapEmbedSources = "always"
                    moduleKind = "commonjs"
                    main = "call"
                }

            }

//            webpackTask {
//                options
//            }
        }
    }


    sourceSets {
        main {
            dependencies {
                //        implementation(project(":muirwik-components"))
//        implementation(npm( "../../link-muirwik-components.js"))

//        implementation(npm("webpack-dev-server"))
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.6.12")
                implementation("org.jetbrains:kotlin-react:16.9.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-react-dom:16.9.0-pre.89-kotlin-1.3.60")
                implementation("io.data2viz:d2v-data2viz-js:0.8.0-RC1")
                implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-css:1.0.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-css-js:1.0.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.89-kotlin-1.3.60")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.11.1")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.1")

//        implementation("com.github.doyaaaaaken:kotlin-csv-js:0.7.3")

                implementation(npm("react", "16.12.0"))
                implementation(npm("react-dom", "16.12.0"))
                implementation(npm("react-draggable"))

                implementation(npm("inline-style-prefixer"))
                implementation(npm("core-js"))
                implementation(npm("@material-ui/core"))
                implementation(npm("@material-ui/icons"))

                implementation(npm("styled-components"))
                implementation(npm("mocha"))
                implementation(npm("karma"))
                implementation(npm("jquery"))

//        implementation(npm("webpack"))
//        implementation(npm("webpack-cli"))


//        implementation(npm("reactstrap"))
//    this one sucks    implementation("kotlin.js.externals:kotlin-js-jquery:3.2.0-0")

            }
        }
    }
}


//val serverPrepare by tasks.creating {
//    dependsOn("bundle")
//}

