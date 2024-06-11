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

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("signing")
    `maven-publish`
}

group = ObfustringData.groupId
version = ObfustringData.core.version

kotlin {
    jvmToolchain(ObfustringData.exampleapp.jvmTarget)
}

java {
    sourceCompatibility = ObfustringData.exampleapp.sourceCompatibility
    targetCompatibility = ObfustringData.exampleapp.targetCompatibility
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    testImplementation(libs.bundles.test.core)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("obfustring-core-release") {
                groupId = ObfustringData.groupId
                artifactId = ObfustringData.core.common.artifactId
                version = ObfustringData.core.version

                if (plugins.hasPlugin("com.android.library")) {
                    from(components["release"])
                } else {
                    from(components["java"])
                }

                pom {
                    name.set(ObfustringData.core.common.artifactId)
                    description.set(ObfustringData.core.common.description)
                    url.set(ObfustringData.core.common.url)

                    licenses {
                        license {
                            name.set(ObfustringData.core.license.licenseName)
                            url.set(ObfustringData.core.license.licenseUrl)
                        }
                    }

                    developers {
                        developer {
                            id.set(ObfustringData.core.developer.developerId)
                            name.set(ObfustringData.core.developer.developerName)
                            email.set(ObfustringData.core.developer.developerEmail)
                        }
                    }

                    scm {
                        connection.set(ObfustringData.core.publish.publishScmConnection)
                        developerConnection.set(
                            ObfustringData.core.publish.publishScmDeveloperConnection
                        )
                        url.set(ObfustringData.core.publish.publishScmUrl)
                    }
                }
            }
        }
        signing {
            sign(publications["obfustring-core-release"])
        }
    }
}