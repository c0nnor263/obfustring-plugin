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

package io.github.c0nnor263.plugin.visitor

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import io.github.c0nnor263.obfustring.core.ObfustringThis
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

abstract class ObfustringVisitorFactory :
    AsmClassVisitorFactory<ObfustringVisitorFactory.InstrumentationParams> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val key = parameters.get().key.get()
        val isLoggingEnabled = parameters.get().loggingEnabled.get()

        return ObfustringClassVisitor(
            key = key,
            isLoggingEnabled = isLoggingEnabled,
            nextClassVisitor = nextClassVisitor
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val isLoggingEnabled = parameters.get().loggingEnabled.get()
        return classData.classAnnotations.any {
            it == ObfustringThis::class.java.name
        }.also { isHasAnnotation ->
            if (isHasAnnotation && isLoggingEnabled) {
                println("\n\t- CLASS: ${classData.className}")
            }
        }
    }

    interface InstrumentationParams : InstrumentationParameters {

        // Key to be used for obfuscation
        @get:Input
        val key: Property<String>

        @get:Input
        val loggingEnabled: Property<Boolean>
    }
}
