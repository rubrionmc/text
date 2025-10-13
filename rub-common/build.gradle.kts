plugins {
    kotlin("jvm")
}
dependencies {
    implementation(project(":api"))
    implementation(libs.common)
    implementation(libs.config)
    compileOnly(libs.jetanno)
    compileOnly(libs.slf4j)
    compileOnly(libs.bundles.adventure)
}

tasks.register<Copy>("exportResources") {
    from("src/main/resources")
    into("${buildDir}/exportedResources")
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(21)
}