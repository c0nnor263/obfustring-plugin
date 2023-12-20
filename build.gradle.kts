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
        classpath("io.github.c0nnor263:plugin:${versions.obfustringVersion}")
    }
}
plugins {
    id("com.android.application") version versions.gradle apply false
    id("com.android.library") version versions.gradle apply false
    id("org.jetbrains.kotlin.android") version versions.kotlin apply false
    id("org.jetbrains.kotlin.jvm") version versions.kotlin apply false
    id("io.github.gradle-nexus.publish-plugin") version versions.nexusPublishPlugin
    id("com.github.ben-manes.versions") version versions.benNamesVersions
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        val version = candidate.version
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any {
            version.uppercase(java.util.Locale.getDefault())
                .contains(it)
        }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        isStable.not()
    }
}

val secretPropsFile = project.rootProject.file("gradle.properties")
val mapOfFields = mutableMapOf<String, Any?>()
if (secretPropsFile.exists()) {
    val properties = Properties()
    FileInputStream(secretPropsFile).use { properties.load(it) }
    properties.forEach { name, value ->
        mapOfFields[name.toString()] = value
    }
}

// Set up Sonatype repository
nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(mapOfFields["sonatypeStagingProfileId"] as String)
            username.set(mapOfFields["ossrhUsername"] as String)
            password.set(mapOfFields["ossrhPassword"] as String)
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            )
        }
    }
}
