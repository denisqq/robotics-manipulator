plugins {
    kotlin("jvm") version "1.4.0"
    kotlin("kapt") version "1.4.21"
    application
}
group = "ru.psu"
version = "1.0-SNAPSHOT"

val tornadofxVersion: String by rootProject
val mapstructVersion: String by rootProject

repositories {
    mavenCentral()
}

application {
    mainClassName = "ru.psu.RoboticsManipulatorApplication"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("no.tornado:tornadofx:$tornadofxVersion")

    implementation("com.fasterxml.jackson.core:jackson-core:2.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")

    implementation("org.mapstruct:mapstruct:${mapstructVersion}")
    kapt("org.mapstruct:mapstruct-processor:${mapstructVersion}")

    testImplementation(kotlin("test-junit"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}