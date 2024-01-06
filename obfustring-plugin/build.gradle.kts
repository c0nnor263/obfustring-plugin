/*
 * Copyright 2024 Oleh Boichuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version Versions.gradlePublish
    id("nu.studer.credentials") version Versions.nuStuderCredentials
}

group = ObfustringData.groupId
version = Versions.obfustringPluginVersion

kotlin {
    jvmToolchain(ObfustringData.exampleapp.jvmTarget)
}

java {
    sourceCompatibility = ObfustringData.exampleapp.sourceCompatibility
    targetCompatibility = ObfustringData.exampleapp.targetCompatibility
}

dependencies {
    implementation(project(":obfustring-core"))
    implementation("com.android.tools.build:gradle-api:${Versions.gradle}")
    implementation("com.android.tools.build:gradle:${Versions.gradle}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
    implementation("org.ow2.asm:asm:${Versions.asm}")
    implementation("org.ow2.asm:asm-commons:${Versions.asm}")
    implementation("org.ow2.asm:asm-util:${Versions.asm}")
    implementation("com.joom.grip:grip:${Versions.joomGrip}")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.Tooling.jupiter}")
    testImplementation("junit:junit:${Versions.Tooling.junit}")
    testImplementation(gradleTestKit())
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    val javaToolchains = project.extensions.getByType<JavaToolchainService>()
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        },
    )
    useJUnitPlatform()

    maxHeapSize = "1G"

    testLogging {
        showStandardStreams = true
    }
}
gradlePlugin {
    val obfustringPlugin by plugins.creating {
        id = ObfustringData.plugin.artifactId
        displayName = ObfustringData.plugin.displayName
        description = ObfustringData.plugin.description
        implementationClass = ObfustringData.plugin.implementationClass
        tags = ObfustringData.plugin.tags
    }
    website.set(ObfustringData.plugin.website)
    vcsUrl.set(ObfustringData.plugin.vcsUrl)
}
