plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.0.0"
    id("nu.studer.credentials") version "3.0"
}

repositories {
    google()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.android.tools.build:gradle-api:7.2.1")
    implementation ("com.github.c0nnor263:obfustring-core:1.1.0")
    gradleApi()
    localGroovy()
}

group = "io.github.c0nnor263"
version = "1.1.0"
gradlePlugin {
// Define the plugin
    plugins {
        create("ObfustringPlugin") {
            id = "io.github.c0nnor263.obfustring-plugin"
            displayName = "Obfustring"
            description = "This plugin obfuscates your strings"
            implementationClass = "ObfustringPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/c0nnor263/Obfustring"
    vcsUrl = "https://github.com/c0nnor263/Obfustring.git"
    tags = listOf("obfuscation", "kotlin", "string")
}