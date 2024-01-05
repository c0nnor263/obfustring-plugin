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

package io.github.c0nnor263.obfustringplugin.visitor

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import io.github.c0nnor263.obfustringcore.ObfustringThis
import io.github.c0nnor263.obfustringplugin.enums.ObfustringMode
import io.github.c0nnor263.obfustringplugin.model.ClassVisitorParams
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

abstract class ObfustringVisitorFactory :
    AsmClassVisitorFactory<ObfustringVisitorFactory.InstrumentationParams> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor,
    ): ClassVisitor {
        val params = ClassVisitorParams.fromInstrumentationParams(parameters.get())
        val verifyClassAdapter = VerifyClassAdapter(nextClassVisitor)
        return ObfustringClassVisitor(
            params = params,
            nextClassVisitor = verifyClassAdapter,
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val mode = parameters.get().mode.get()
        return when (mode) {
            ObfustringMode.DEFAULT -> {
                classData.classAnnotations.any {
                    it == ObfustringThis::class.java.name
                }
            }

            ObfustringMode.FORCE -> true
            ObfustringMode.DISABLED -> false
            else -> false
        }.also { isReadyForProcessing ->
            if (isReadyForProcessing) {
                println("\n\t- CLASS: ${classData.className}")
            }
        }
    }

    internal interface InstrumentationParams : InstrumentationParameters {
        /**
         * Key to use for obfuscation
         */
        @get:Input
        val key: Property<String>

        /**
         * Strategy to use for string concatenation
         */
        @get:Input
        val loggingEnabled: Property<Boolean>

        /**
         * Obfustring mode
         */
        @get:Input
        val mode: Property<ObfustringMode>
    }
}
