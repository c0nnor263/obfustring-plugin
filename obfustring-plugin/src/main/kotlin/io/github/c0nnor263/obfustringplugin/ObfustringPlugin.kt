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
import io.github.c0nnor263.obfustringcore.Obfustring
import io.github.c0nnor263.obfustringplugin.enums.isEnabled
import io.github.c0nnor263.obfustringplugin.log.ObfustringLogger
import io.github.c0nnor263.obfustringplugin.transform.ObfustringTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.JavaPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// TODO: Secure obfuscation key
// TODO: Implement encrypt/decrypt methods
class ObfustringPlugin : Plugin<Project> {
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
        pluginExtension =
            project.extensions.create(
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
            EXCEPTION_INIT_OBFUSTRING_TRANSFORM
        }

        val transform = ObfustringTransform(androidComponentsExtension)
        transform.configureInstrumentationParamsConfig { params ->
            params.apply {
                key.set(pluginExtension.key)
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
        logger = ObfustringLogger(pluginExtension.loggingEnabled, project.logger)
        logger.quiet(LOG_INIT_WITH_KEY(pluginExtension.key))

        val customObfustring = pluginExtension.customObfustring
        if (customObfustring !is Obfustring) {
            logger.quiet(LOG_INIT_WITH_CUSTOM_OBFUSTRING(customObfustring::class.java.simpleName))
        }
    }

    companion object {
        lateinit var pluginExtension: ObfustringExtension
        lateinit var logger: Logger
        const val VERSION: String = "12.0.2"

        const val EXCEPTION_INIT_OBFUSTRING_TRANSFORM = "Obfustring | Project is not an Android project"
        const val EXCEPTION_INVALID_CUSTOM_OBFUSTRING =
            "Obfustring | Error: Custom Obfustring must be an object instance. Please provide a valid Custom Obfustring"
        val LOG_INIT_WITH_KEY: (String) -> String = { "Obfustring | KEY: $it" }
        val LOG_INIT_WITH_CUSTOM_OBFUSTRING: (String) -> String = { "Obfustring | CUSTOM_OBFUSTRING: $it" }
    }
}