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

package io.github.c0nnor263.obfustring.core.model

import io.github.c0nnor263.obfustring.core.ObfustringMode
import io.github.c0nnor263.obfustring.core.enums.AlphabeticCase

class Crypto {
    companion object {
        private const val ENCRYPT_INT = 90
        private const val DECRYPT_INT = 26
        private const val UPPER_CASE_DECRYPT = 38

        fun Int.crypto(
            case: AlphabeticCase,
            keyData: KeyData,
            mode: Int
        ): Char {
            val encryptInt = ENCRYPT_INT
            val decryptInt = DECRYPT_INT + when (case) {
                AlphabeticCase.UPPER_CASE -> UPPER_CASE_DECRYPT
                else -> 0
            }

            val keyCharCode = keyData.getCode()
            val processedValue = if (mode == ObfustringMode.ENCRYPT) {
                (this + keyCharCode - encryptInt) % DECRYPT_INT
            } else {
                (this - keyCharCode + decryptInt) % DECRYPT_INT
            }.plus(case.startCode).toChar()

            keyData.increase()
            return processedValue
        }
    }
}
