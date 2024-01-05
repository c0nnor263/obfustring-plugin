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

import io.github.c0nnor263.obfustringplugin.enums.ObfustringMode
import io.github.c0nnor263.obfustringplugin.enums.StringConcatStrategy

abstract class ObfustringExtension {
    /**
     * Key used to obfuscate strings. By default it is generated randomly using [generateRandomKey]
     *
     * If the key contains unsupported characters, they will be replaced
     */
    var key: String = generateRandomKey()
        set(value) {
            field = verifyKey(value)
        }

    /**
     * Configure obfustring mode using [ObfustringMode]
     *
     * Default value is [ObfustringMode.DEFAULT]
     */
    var mode: ObfustringMode = ObfustringMode.DEFAULT

    /**
     * Enable logging
     */
    var loggingEnabled: Boolean = true

    /**
     * Set JVM argument -Xstring-concat using [StringConcatStrategy]
     *
     * Default value is [StringConcatStrategy.INDY]
     */
    var stringConcatStrategy: StringConcatStrategy = StringConcatStrategy.INDY

    companion object {
        const val CONFIGURATION_NAME = "obfustring"
    }
}
