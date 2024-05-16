pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "fs-pgn"

include(":fs-pgn")
//include(":fs-pgn-serialization-ktor")
include(":fs-pgn-testtools")
