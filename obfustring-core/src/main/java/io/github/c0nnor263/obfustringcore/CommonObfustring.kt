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

package io.github.c0nnor263.obfustringcore

/**
 * Common obfustring interface
 */
fun interface CommonObfustring {
    /**
     * Process the string
     *
     * @param key the key for obfuscation
     * @param stringValue the string value to be processed
     * @param mode the mode of obfuscation. [ObfustringCryptoMode.ENCRYPT] or [ObfustringCryptoMode.DECRYPT]
     * @return the processed string
     */
    fun process(
        key: String,
        stringValue: String,
        mode: Int
    ): String
}