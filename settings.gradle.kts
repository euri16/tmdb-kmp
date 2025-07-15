enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TMDB_Multiplatform"
include(":androidApp")
include(":mcp-server")
include(":core:network")
include(":core:test")
include(":core:utils")
include(":data:movies")
include(":data:common")
include(":core:models")
