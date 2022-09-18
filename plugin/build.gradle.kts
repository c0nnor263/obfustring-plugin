plugins {
    `maven-publish`
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.0.0"
    id("nu.studer.credentials") version "3.0"
}

group = "io.github.c0nnor263"
version = "1.5.7"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    implementation("com.android.tools.build:gradle-api:7.2.2")
}

gradlePlugin {
    // Define the plugin
    val obfustringPlugin by plugins.creating {
        id = "io.github.c0nnor263.obfustring-plugin"
        displayName = "Obfustring"
        description = "This plugin obfuscates your strings"
        implementationClass = "io.github.c0nnor263.ObfustringPlugin"
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