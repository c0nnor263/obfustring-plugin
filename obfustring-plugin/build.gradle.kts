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
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.studerCredentials)
}

group = ObfustringData.groupId
version = ObfustringData.plugin.version

kotlin {
    jvmToolchain(ObfustringData.exampleapp.jvmTarget)
}

dependencies {
    implementation(projects.obfustringCore)
    implementation(libs.bundles.obfustring.plugin)

    testImplementation(libs.bundles.test.core)
    testImplementation(gradleTestKit())
}

tasks.test {
    val javaToolchains = project.extensions.getByType<JavaToolchainService>()
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    )
    useJUnitPlatform()
}

gradlePlugin {
    website.set(ObfustringData.plugin.website)
    vcsUrl.set(ObfustringData.plugin.vcsUrl)
    val obfustringPlugin by plugins.creating {
        id = ObfustringData.plugin.artifactId
        displayName = ObfustringData.plugin.displayName
        description = ObfustringData.plugin.description
        implementationClass = ObfustringData.plugin.implementationClass
        tags = ObfustringData.plugin.tags
    }
}