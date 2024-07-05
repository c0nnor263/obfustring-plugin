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

@file:Suppress("ClassName", "SpellCheckingInspection", "ktlint:standard:filename")

import org.gradle.api.JavaVersion

@Suppress("ktlint:standard:property-naming")
object ObfustringData {
    private const val repositoryUrl = "github.com/c0nnor263/obfustring-plugin"
    const val groupId = "io.github.c0nnor263"

    object exampleapp {
        const val namespace = "com.conboi.exampleapp"
        const val compileSdk = 34
        const val minSdk = 24
        const val jvmTarget = 11
        val sourceCompatibility = JavaVersion.VERSION_11
        val targetCompatibility = JavaVersion.VERSION_11
    }

    object core {
        const val version = "12.0.2"
        object common {
            const val artifactId = "obfustring-core"
            const val description = "Required core dependency for obfustring-plugin"
            const val url = "https://$repositoryUrl"
        }

        object license {
            const val licenseName = "Apache License"
            const val licenseUrl = "https://$repositoryUrl/blob/master/LICENSE"
        }

        object developer {
            const val developerId = "c0nnor263"
            const val developerName = "c0nnor263"
            const val developerEmail = "bojchu7@gmail.com"
        }

        object publish {
            const val publishScmConnection = "scm:git:$repositoryUrl.git"
            const val publishScmDeveloperConnection = "scm:git:ssh://$repositoryUrl.git"
            const val publishScmUrl = "https://$repositoryUrl/tree/master"
        }
    }

    object plugin {
        const val version = "12.0.2"
        const val artifactId = "$groupId.obfustring-plugin"
        const val website = "https://$repositoryUrl"

        const val vcsUrl = "https://$repositoryUrl.git"
        const val displayName = "Obfustring"
        const val description =
            "This is a Android Gradle plugin that obfuscates strings in Kotlin classes"
        const val implementationClass = "$groupId.obfustringplugin.ObfustringPlugin"

        val tags: List<String> =
            listOf(
                "android",
                "kotlin",
                "security",
                "obfuscation",
                "android-development",
                "obfuscator",
                "string-manipulation",
                "android-app",
                "string-obfuscation",
                "security-tools",
                "obfustring",
                "obfuscate",
                "strings"
            )
    }
}