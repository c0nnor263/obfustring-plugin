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

package io.github.c0nnor263.obfustringplugin.enums

import io.github.c0nnor263.obfustringcore.ObfustringThis

enum class ObfustringMode {
    /**
     * Default mode. Obfustring will be applied only to classes annotated with [ObfustringThis]
     */
    DEFAULT,

    /**
     * Force mode. Obfustring will be applied to all classes
     */
    FORCE,

    /**
     * Disabled mode. Obfustring will not be applied
     */
    DISABLED
}

fun ObfustringMode.isEnabled(): Boolean = this != ObfustringMode.DISABLED