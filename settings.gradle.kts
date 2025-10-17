import java.net.URL
import java.nio.file.Files

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven("https://rubrionmc.github.io/repository/")
    }

    versionCatalogs {
        create("libs") {
            val remoteUrl = "https://rubrionmc.github.io/repository/resources/libs.versions.toml"
            val localFile = file("$rootDir/.gradle/tmp-libs.versions.toml")

            if (localFile.exists()) {
                localFile.delete()
            }

            println("Load global libs.versions.toml...")
            localFile.parentFile.mkdirs()

            @Suppress("DEPRECATION")
            URL(remoteUrl).openStream().use { input ->
                Files.copy(input, localFile.toPath())
            }

            from(files(localFile))
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

rootProject.name = "rub-text"

include("api", "common")

project(":api").projectDir = file("rub-api")
project(":common").projectDir = file("rub-common")