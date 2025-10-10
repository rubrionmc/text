dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins { kotlin("jvm") version "2.2.10" }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "rub-text"

include("api", "common")

project(":api").projectDir = file("rub-api")
project(":common").projectDir = file("rub-common")