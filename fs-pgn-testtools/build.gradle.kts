import fsryan.shouldConfigureAndroid
import fsryan.shouldConfigureIOS
import fsryan.shouldConfigureJs
import fsryan.shouldConfigureJvm

plugins {
    id("maven-publish")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
}

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
            nodejs()
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
            api(project(":fs-pgn"))
            api(libs.benasher.uuid)
        }
    }
}