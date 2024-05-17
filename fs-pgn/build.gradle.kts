import fsryan.shouldConfigureAndroid
import fsryan.shouldConfigureIOS
import fsryan.shouldConfigureJs
import fsryan.shouldConfigureJvm

plugins {
    id("maven-publish")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
}

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
            api(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":fs-pgn-testtools"))
        }
    }
}
