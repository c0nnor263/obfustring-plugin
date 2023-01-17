plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.0.0"
    id("nu.studer.credentials") version "3.0"
}

group = "io.github.c0nnor263"
version = "23.01.11"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    implementation("com.android.tools.build:gradle-api:7.3.1")
    implementation(project(":obfustring-core"))

}

gradlePlugin {
    plugins.creating {
        id = "io.github.c0nnor263.obfustring-plugin"
        displayName = "Obfustring"
        description = "This plugin obfuscates your strings"
        implementationClass = "io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.ObfustringPlugin"
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