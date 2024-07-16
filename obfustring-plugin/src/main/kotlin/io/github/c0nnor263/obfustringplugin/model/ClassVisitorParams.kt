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

import io.github.c0nnor263.obfustringplugin.enums.ObfustringMode
import io.github.c0nnor263.obfustringplugin.visitor.ObfustringVisitorFactory

internal data class ClassVisitorParams(
    val key: String,
    val mode: ObfustringMode,
    var isInstrumentable: Boolean = false
) {
    constructor(
        params: ObfustringVisitorFactory.InstrumentationParams
    ) : this(
        key = params.key.get(),
        mode = params.mode.get()
    )
}