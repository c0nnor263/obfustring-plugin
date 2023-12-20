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

package io.github.c0nnor263.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import io.github.c0nnor263.plugin.visitor.ObfustringVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class ObfustringPlugin : Plugin<Project> {
    private lateinit var obfustringExtension: ObfustringExtension

    override fun apply(project: Project) {
        initPlugin(project)
        if (!obfustringExtension.isEnabled) {
            return
        }
        if (obfustringExtension.loggingEnabled) {
            println("Obfustring | KEY: ${obfustringExtension.key}")
        }

        val androidComponentsExtension =
            project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponentsExtension.onVariants { variant ->
            variant.instrumentation.apply {
                setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
                )
                transformClassesWith(
                    ObfustringVisitorFactory::class.java,
                    // TODO Add option to choose which classes to obfuscate
                    InstrumentationScope.ALL
                ) { params ->
                    params.key.set(obfustringExtension.key)
                    params.loggingEnabled.set(obfustringExtension.loggingEnabled)
                }
            }
        }
    }

    private fun initPlugin(project: Project) = with(project) {
        dependencies.add(
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            "io.github.c0nnor263:obfustring-core:23.01.11"
        )
        obfustringExtension = extensions.create(
            ObfustringExtension.CONFIGURATION_NAME,
            ObfustringExtension::class.java
        )
    }
}
