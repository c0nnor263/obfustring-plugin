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

package io.github.c0nnor263.obfustringplugin

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.github.c0nnor263.obfustringcore.Obfustring
import io.github.c0nnor263.obfustringplugin.enums.ObfustringMode
import io.github.c0nnor263.obfustringplugin.enums.StringConcatStrategy
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.internal.impldep.org.hamcrest.MatcherAssert.assertThat
import org.gradle.internal.impldep.org.hamcrest.core.IsNull
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class ObfustringPluginTest {
    @TempDir
    var testProjectDir: File? = null

    private lateinit var project: Project
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setup() {
        outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        project = ProjectBuilder.builder().withProjectDir(testProjectDir).build()
        project.pluginManager.apply {
            apply("com.android.application")
            apply("kotlin-android")
            ObfustringPlugin().apply(project)
        }
        project.extensions.getByType(BaseAppModuleExtension::class.java).run {
            compileSdk = 34
            namespace = "io.github.c0nnor263.obfustringplugin_test"

            defaultConfig {
                applicationId = namespace
                minSdk = 24
                versionCode = 1
                versionName = "1.0"

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            buildTypes {
                release {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                    signingConfig = signingConfigs.getByName("debug")
                }
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }

    @Test
    fun configurePluginWithExtension_exists() =
        with(project) {
            assertThat(
                extensions.getByType(ObfustringExtension::class.java),
                IsNull.notNullValue()
            )
        }

    @Test
    fun configurePluginWithObfustringCore_exists() =
        with(project) {
            assertThat(
                configurations.getByName("implementation").dependencies.find {
                    it.name == "obfustring-core"
                },
                IsNull.notNullValue()
            )
        }

    @Test
    fun configureObfustringExtension_emptyKotlinOption() =
        with(project) {
            extensions.getByType(ObfustringExtension::class.java).run {
                stringConcatStrategy = StringConcatStrategy.INDY
                mode = ObfustringMode.DISABLED
            }
            evaluationDependsOn(":")
            tasks.withType(KotlinCompile::class.java).configureEach { task ->
                task.kotlinOptions {
                    assert(freeCompilerArgs.isEmpty())
                }
            }
        }

    @Test
    fun configureObfustringExtension_notEmptyKotlinOption() =
        with(project) {
            val concatStrategy = StringConcatStrategy.INLINE
            extensions.getByType(ObfustringExtension::class.java).run {
                stringConcatStrategy = concatStrategy
                mode = ObfustringMode.DEFAULT
            }
            evaluationDependsOn(":")
            tasks.withType(KotlinCompile::class.java).configureEach { task ->
                task.kotlinOptions {
                    assert(freeCompilerArgs.any { it == concatStrategy.rawArgument })
                }
            }
        }

    @Test
    fun configureObfustringExtensions_loggingEnabled() =
        with(project) {
            val extension =
                extensions.getByType(ObfustringExtension::class.java).apply {
                    loggingEnabled = true
                }
            evaluationDependsOn(":")
            val result =
                outputStream.toString()
                    .contains("${Obfustring::class.java.simpleName} | KEY: ${extension.key}")
            assert(result)
        }

    @Test
    fun configureObfustringExtensions_loggingDisabled() =
        with(project) {
            val extension =
                extensions.getByType(ObfustringExtension::class.java).apply {
                    loggingEnabled = false
                }
            evaluationDependsOn(":")
            val result =
                outputStream.toString()
                    .contains("${Obfustring::class.java.simpleName} | KEY: ${extension.key}")
            assert(!result)
        }
}