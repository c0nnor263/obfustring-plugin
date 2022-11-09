plugins {
    `maven-publish`
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.0.0"
    id("nu.studer.credentials") version "3.0"
}

group = "io.github.c0nnor263"
version = "11.09"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    implementation("com.android.tools.build:gradle-api:7.3.1")
}

gradlePlugin {
    // Define the plugin
    val obfustringPlugin by plugins.creating {
        id = "io.github.c0nnor263.obfustring-plugin"
        displayName = "Obfustring"
        description = "This plugin obfuscates your strings"
        implementationClass = "io.github.boiawidmb9mb12095n21b50215b16132.ObfustringPlugin"
    }
}

pluginBundle {
    website = "https://github.com/c0nnor263/obfustring-plugin"
    vcsUrl = "https://github.com/c0nnor263/obfustring-plugin.git"
    tags = listOf("obfuscation", "kotlin", "string")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}