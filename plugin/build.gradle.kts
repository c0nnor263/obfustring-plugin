/*
 * Copyright 2023 Oleh Boichuk
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

/*
* Copyright 2023 Oleh Boichuk
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

plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version versions.gradlePublish
    id("nu.studer.credentials") version versions.nuStuderCredentials
}

group = ObfustringData.groupId
version = versions.obfustringVersion

kotlin {
    jvmToolchain(ObfustringData.config.jvmTarget)
}

java {
    sourceCompatibility = ObfustringData.config.sourceCompatibility
    targetCompatibility = ObfustringData.config.targetCompatibility
}

dependencies {
    implementation(project(":obfustring-core"))
    implementation("com.android.tools.build:gradle-api:${versions.gradle}")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}")
    implementation("org.ow2.asm:asm:${versions.asm}")
    implementation("org.ow2.asm:asm-commons:${versions.asm}")
    implementation("com.joom.grip:grip:${versions.joomGrip}")
}

gradlePlugin {
    val obfustringPlugin by plugins.creating {
        id = ObfustringData.plugin.id
        displayName = ObfustringData.plugin.displayName
        description = ObfustringData.plugin.description
        implementationClass = ObfustringData.plugin.implementationClass
    }
    website.set(ObfustringData.plugin.website)
    vcsUrl.set(ObfustringData.plugin.vcsUrl)
}
