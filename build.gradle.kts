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
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.c0nnor263:obfustring-plugin:12.0.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.nexusPublish)
    alias(libs.plugins.benMames)
    alias(libs.plugins.ktlint) apply true
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply<com.github.benmanes.gradle.versions.VersionsPlugin>()


    // Ktlint configuration
    configure<KtlintExtension> {
        android = false // to use the Android Studio KtLint plugin style
        ignoreFailures = true

        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
    afterEvaluate {
        tasks.findByName("preBuild")?.dependsOn("ktlintFormat")
    }

    // Never mind about this, it's just a helper function to check newest versions of dependencies
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
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
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            )
        }
    }
}