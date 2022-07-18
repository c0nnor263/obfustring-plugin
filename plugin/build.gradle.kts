plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    id("com.gradle.plugin-publish") version "1.0.0"
    id("nu.studer.credentials") version "3.0"
}
group = "io.github.c0nnor263"
version = "1.4.7"

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.android.tools.build:gradle-api:7.2.1")
    implementation("com.github.c0nnor263:obfustring-core:1.2.5")
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