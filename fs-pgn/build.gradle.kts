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
            publishLibraryVariants("release")
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
            api(libs.okio)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.okio.fakefilesystem)
        }

        jsMain.dependencies {
            implementation(libs.okio.nodefilesystem)
        }
    }
}
