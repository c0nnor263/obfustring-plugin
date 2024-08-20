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

package io.github.c0nnor263.obfustringplugin.model

import com.android.build.api.instrumentation.ClassData
import io.github.c0nnor263.obfustringcore.annotations.ObfustringExclude
import io.github.c0nnor263.obfustringcore.annotations.ObfustringThis
import io.github.c0nnor263.obfustringplugin.enums.ObfustringMode

internal data class ClassDataHandler(private val data: ClassData) {
    private val annotations = data.classAnnotations
    val name = data.className

    fun getQualifiedName(): String {
        return name.replace(".", "/")
    }

    fun checkIfInstrumentable(params: ClassVisitorParams) {
        val mode = params.mode
        val result =
            when (mode) {
                ObfustringMode.DEFAULT -> {
                    annotations.any {
                        it == ObfustringThis::class.java.name
                    } &&
                        annotations.none {
                            it == ObfustringExclude::class.java.name
                        }
                }

                ObfustringMode.FORCE -> {
                    annotations.none {
                        it == ObfustringExclude::class.java.name
                    }
                }

                ObfustringMode.DISABLED -> false
            }
        params.isInstrumentable = result
    }
}