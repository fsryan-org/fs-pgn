pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "fs-pgn"

include(":fs-pgn")                  // <-- 1a. serialization-independent representation
include(":fs-pgn-serialization")    // <-- 2. framework-independent serialization
// include(":fs-pgn-serialization-ktor")         // <-- 3. Plugin to ktor client-server framework
include(":fs-pgn-testtools")        // <-- 1b. test tools
