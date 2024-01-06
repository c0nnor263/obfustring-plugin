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

package io.github.c0nnor263.obfustringplugin.transform

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import io.github.c0nnor263.obfustringplugin.visitor.ObfustringVisitorFactory

internal class ObfustringTransform(private val androidComponents: AndroidComponentsExtension<*, *, *>) {
    companion object {
        const val VARIANT_SELECTOR_RELEASE = "release"
    }

    fun configureInstrumentationParamsConfig(parametersBlock: (ObfustringVisitorFactory.InstrumentationParams) -> Unit) {
        val releaseSelector = androidComponents.selector().withName(VARIANT_SELECTOR_RELEASE)
        androidComponents.onVariants(selector = releaseSelector) { variant ->
            with(variant.instrumentation) {
                setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS,
                )
                transformClassesWith(
                    ObfustringVisitorFactory::class.java,
                    InstrumentationScope.PROJECT,
                    parametersBlock,
                )
            }
        }
    }
}
