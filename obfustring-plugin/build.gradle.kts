plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.0.0"
    id("nu.studer.credentials") version "3.0"
}

dependencies {
    implementation("com.android.tools.build:gradle-api:7.2.1")
    implementation ("com.github.c0nnor263:obfustring-core:1.0.1")
    gradleApi()
    localGroovy()
}

group = "io.github.c0nnor263"
version = "1.0.9"
gradlePlugin {
    // Define the plugin
    plugins {
        create("ObfustringPlugin") {
            id = "io.github.c0nnor263.obfustring-core"
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