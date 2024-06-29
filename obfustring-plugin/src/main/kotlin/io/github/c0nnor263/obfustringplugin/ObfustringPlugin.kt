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

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.crash.afterEvaluate
import io.github.c0nnor263.obfustringcore.Obfustring
import io.github.c0nnor263.obfustringplugin.enums.isEnabled
import io.github.c0nnor263.obfustringplugin.transform.ObfustringTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// TODO: Secure obfuscation key
// TODO: Implement Logger
// TODO: Implement encrypt/decrypt methods
class ObfustringPlugin : Plugin<Project> {
    companion object {
        lateinit var pluginExtension: ObfustringExtension
        const val VERSION: String = "12.0.2"
    }

    private lateinit var project: Project

    override fun apply(project: Project) {
        this.project = project
        initPlugin()
        initObfustringTransform()
        project.afterEvaluate {
            if (pluginExtension.mode.isEnabled()) {
                setupBuildSrc()
                setupKotlinCompileOptions()
                setupLogging()
            }
        }
    }

    private fun initPlugin() {
        pluginExtension = project.extensions.create(
            ObfustringExtension.CONFIGURATION_NAME,
            ObfustringExtension::class.java
        )
        project.dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            "io.github.c0nnor263:obfustring-core:$VERSION"
        )
    }

    private fun initObfustringTransform() {
        val androidComponentsExtension =
            project.extensions.findByType(AndroidComponentsExtension::class.java)

        requireNotNull(androidComponentsExtension) {
            "${Obfustring.NAME} | Project is not an Android project"
        }

        val transform = ObfustringTransform(androidComponentsExtension)
        transform.configureInstrumentationParamsConfig { params ->
            params.apply {
                key.set(pluginExtension.key)
                loggingEnabled.set(pluginExtension.loggingEnabled)
                mode.set(pluginExtension.mode)
            }
        }
    }

    private fun setupBuildSrc() {
        val kotlinExtension = project.extensions.getByType(KotlinAndroidProjectExtension::class.java)
        val kotlinMainSource = kotlinExtension.sourceSets.getAt("main").kotlin
        kotlinMainSource.srcDirs(
            kotlinMainSource.srcDirs + project.layout.files("${project.rootDir}/buildSrc/src/main/kotlin/obfustring/")
        )
    }

    private fun setupKotlinCompileOptions() {
        val strategyArgument = pluginExtension.stringConcatStrategy.rawArgument
        project.tasks.withType(KotlinCompilationTask::class.java).configureEach { task ->
            task.compilerOptions {
                freeCompilerArgs.add(strategyArgument)
            }
        }
    }

    private fun setupLogging() {
        if (!pluginExtension.loggingEnabled) {
            return
        }
        project.logging.captureStandardOutput(LogLevel.INFO)
        project.logging.captureStandardError(LogLevel.ERROR)
        println("${Obfustring.NAME} | KEY: ${pluginExtension.key}")

        val customObfustring = pluginExtension.customObfustring

        if (customObfustring !is Obfustring) {
            println("${Obfustring.NAME} | CUSTOM_OBFUSTRING: ${customObfustring::class.simpleName}")
        }
    }
}