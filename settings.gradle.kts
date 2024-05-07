pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "fs-pgn"

include(":fs-pgn-api")
include(":fs-pgn-api-kxs")
include(":fs-pgn-testtools")
