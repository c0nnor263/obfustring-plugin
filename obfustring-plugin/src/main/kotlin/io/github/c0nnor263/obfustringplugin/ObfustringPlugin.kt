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
import io.github.c0nnor263.obfustringcore.CommonObfustring
import io.github.c0nnor263.obfustringcore.Obfustring
import io.github.c0nnor263.obfustringplugin.enums.isEnabled
import io.github.c0nnor263.obfustringplugin.transform.ObfustringTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// TODO: Write some tests for new functionality
// TODO: Write test for checking obfustring directory in buildSrc
// TODO: Add class names exclude list
// TODO: Add @ObfustringExclude annotation

class ObfustringPlugin : Plugin<Project> {
    companion object {
        lateinit var mainObfustring: CommonObfustring
        const val VERSION: String = "12.0.2"
    }

    private lateinit var obfustringExtension: ObfustringExtension

    override fun apply(project: Project) {
        initPlugin(project)
        initObfustringTransform(project)
    }

    private fun initPlugin(project: Project) =
        with(project) {
            obfustringExtension =
                extensions.create(ObfustringExtension.CONFIGURATION_NAME, ObfustringExtension::class.java)
            dependencies.add(
                JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
                "io.github.c0nnor263:obfustring-core:$VERSION"
            )
        }

    private fun initObfustringTransform(project: Project) {
        val androidComponentsExtension =
            project.extensions.getByType(AndroidComponentsExtension::class.java)

        val transform = ObfustringTransform(androidComponentsExtension)
        transform.configureInstrumentationParamsConfig { params ->
            params.apply {
                key.set(obfustringExtension.key)
                loggingEnabled.set(obfustringExtension.loggingEnabled)
                mode.set(obfustringExtension.mode)
                mainObfustring = checkCustomObfustring(obfustringExtension.customObfustring)
            }
            if (obfustringExtension.mode.isEnabled()) {
                setupBuildSrc(project)
                setupKotlinCompileOptions(project)
                setupLogging(project)
            }
        }
    }

    private fun setupBuildSrc(project: Project) {
        val kotlin = project.extensions.getByType(KotlinAndroidProjectExtension::class.java)
        val originalSrcDirs = kotlin.sourceSets.getAt("main").kotlin.srcDirs
        kotlin.sourceSets.getAt("main").kotlin.srcDirs(
            originalSrcDirs + project.layout.files("${project.rootDir}/buildSrc/src/main/kotlin/obfustring/")
        )
    }

    private fun setupKotlinCompileOptions(project: Project) =
        with(project) {
            val strategyArgument = obfustringExtension.stringConcatStrategy.rawArgument
            tasks.withType(KotlinCompilationTask::class.java).configureEach { task ->
                task.compilerOptions {
                    freeCompilerArgs.add(strategyArgument)
                }
            }
        }

    private fun setupLogging(project: Project) =
        with(project) {
            if (obfustringExtension.loggingEnabled) {
                logging.captureStandardOutput(LogLevel.INFO)
                logging.captureStandardError(LogLevel.ERROR)
                println("${Obfustring.NAME} | KEY: ${obfustringExtension.key}")
                if (mainObfustring !is Obfustring) {
                    println("${Obfustring.NAME} | CUSTOM_OBFUSTRING: ${mainObfustring::class.simpleName}")
                }
            }
        }
}