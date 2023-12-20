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

package io.github.c0nnor263.obfustring.core

import io.github.c0nnor263.obfustring.core.Obfustring.process
import io.github.c0nnor263.obfustring.core.enums.AppendOperation
import io.github.c0nnor263.obfustring.core.enums.SpecificSymbol
import io.github.c0nnor263.obfustring.core.enums.alphabeticCase
import io.github.c0nnor263.obfustring.core.model.Crypto.Companion.crypto
import io.github.c0nnor263.obfustring.core.model.KeyData

/**
 * Obfuscates class strings that have the [ObfustringThis] annotation using the Vigenère cipher.
 *
 * [process] - main method for obfuscation
 */
object Obfustring {

    private var isPassNow: Boolean = false

    @JvmStatic
    fun process(
        key: String,
        stringValue: String,
        mode: Int = ObfustringMode.DECRYPT
    ): String =
        with(stringValue) {
            if (stringValue.isBlank()) return stringValue
            require(key.isNotBlank()) {
                "Key must not be empty for ${Obfustring::class.java.name}"
            }

            val stringBuilder = StringBuilder()
            val keyData = KeyData(key)

            stringValue.forEach stringIterator@{ char ->
                if (isPassNow) {
                    stringBuilder.append(char)
                    isPassNow = false
                    return@stringIterator
                }

                val result = char.tryProcess(keyData, mode)
                if (result != null) {
                    stringBuilder.append(result)
                    return@stringIterator
                }

                when (char.checkForSpecificSymbol()) {
                    AppendOperation.SKIP -> {
                        stringBuilder.append(char)
                        isPassNow = true
                    }

                    else -> stringBuilder.append(char)
                }
            }

            return stringBuilder.toString()
        }

    private fun Char.checkForSpecificSymbol(): AppendOperation {
        return when (this) {
            SpecificSymbol.BACKSLASH.symbol -> AppendOperation.SKIP
            else -> AppendOperation.APPEND
        }
    }

    private fun Char.tryProcess(keyData: KeyData, mode: Int): Char? {
        return alphabeticCase()?.let { case ->
            code.crypto(
                case,
                keyData,
                mode
            )
        }
    }
}
