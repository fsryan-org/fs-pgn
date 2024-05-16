import fsryan.shouldConfigureAndroid
import fsryan.shouldConfigureIOS
import fsryan.shouldConfigureJs
import fsryan.shouldConfigureJvm

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
//    alias(libs.plugins.kover)
}

group = rootProject.group
version = rootProject.version

kotlin {

    if (shouldConfigureAndroid()) {
        androidTarget {
            publishLibraryVariants(
                "release")
        }
    }
    if (shouldConfigureJvm()) {
        jvm()
        jvmToolchain(17)
    }
    if (shouldConfigureJs()) {
        js(IR) {
            moduleName = project.name
            nodejs {
                testTask {
                    useMocha()
                    testLogging.showStandardStreams = true
                }
            }
            useCommonJs()
            binaries.library()
            generateTypeScriptDefinitions()
        }
    }
    if (shouldConfigureIOS()) {
        iosArm64()
        iosSimulatorArm64()
        iosX64()
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":fs-pgn-serialization"))
            api(libs.ktor.serialization)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":fs-pgn-testtools"))
            implementation(libs.okio.fakefilesystem)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.cio)
            implementation(libs.kotlinx.coroutines.test)
        }

        jsMain.dependencies {
            implementation(libs.okio.nodefilesystem)
        }
    }
}