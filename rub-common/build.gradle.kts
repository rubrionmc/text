plugins {
    kotlin("jvm")
}
dependencies {
    implementation(project(":api"))
    implementation(libs.common)
    implementation(libs.config)
    implementation(libs.commonConfig)
    compileOnly(libs.jetanno)
    compileOnly(libs.slf4j)
    implementation(libs.bundles.adventure)
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