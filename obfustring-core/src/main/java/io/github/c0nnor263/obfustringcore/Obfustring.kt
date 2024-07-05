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

import io.github.c0nnor263.obfustringcore.Obfustring.process
import io.github.c0nnor263.obfustringcore.annotations.ObfustringThis
import io.github.c0nnor263.obfustringcore.model.KeyData

/**
 * Obfuscates strings from class that have the [ObfustringThis] annotation using the Vigenère cipher.
 *
 * [process] - main method for obfuscation
 */
object Obfustring : CommonObfustring {
    const val EMPTY_KEY_MSG = "Obfustring | Key must not be empty"

    override fun process(
        key: String,
        stringValue: String,
        mode: Int
    ): String {
        if (stringValue.isBlank()) return stringValue
        require(key.isNotBlank()) { EMPTY_KEY_MSG }

        val stringBuilder = StringBuilder()
        val keyData = KeyData(key)

        stringValue.forEach { char ->
            val processedChar = char.tryProcess(keyData, mode)
            val result = processedChar ?: char
            stringBuilder.append(result)
        }

        return stringBuilder.toString()
    }
}