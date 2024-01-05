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

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.io.FileInputStream
import java.util.Properties

buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.c0nnor263:obfustring-plugin:${Versions.obfustringVersion}")
    }
}
plugins {
    id("com.android.application") version Versions.gradle apply false
    id("com.android.library") version Versions.gradle apply false
    id("org.jetbrains.kotlin.android") version Versions.kotlin apply false
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin apply false
    id("io.github.gradle-nexus.publish-plugin") version Versions.nexusPublishPlugin
    id("com.github.ben-manes.versions") version Versions.benNamesVersions
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        val version = candidate.version
        val stableKeyword =
            listOf("RELEASE", "FINAL", "GA").any {
                version.uppercase(java.util.Locale.getDefault())
                    .contains(it)
            }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        isStable.not()
    }
}

var sonatypeStagingProfileId: String? = null
var ossrhUsername: String? = null
var ossrhPassword: String? = null
val secretPropsFile: File = project.rootProject.file("gradle.properties")
if (secretPropsFile.exists()) {
    val properties = Properties()
    FileInputStream(secretPropsFile).use { properties.load(it) }
    sonatypeStagingProfileId = properties.getProperty("sonatypeStagingProfileId")
    ossrhUsername = properties.getProperty("ossrhUsername")
    ossrhPassword = properties.getProperty("ossrhPassword")
}

// Set up Sonatype repository
nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(sonatypeStagingProfileId)
            username.set(ossrhUsername)
            password.set(ossrhPassword)
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"),
            )
        }
    }
}
