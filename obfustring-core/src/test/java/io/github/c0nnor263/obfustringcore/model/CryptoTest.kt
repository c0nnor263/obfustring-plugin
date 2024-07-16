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

package io.github.c0nnor263.obfustringcore.model

import io.github.c0nnor263.obfustringcore.ObfustringCryptoMode
import io.github.c0nnor263.obfustringcore.alphabeticCase
import io.github.c0nnor263.obfustringcore.model.Crypto.Companion.crypto
import org.junit.jupiter.api.Test

class CryptoTest {
    private val testChar = 'A'
    private val charCase = testChar.alphabeticCase() ?: throw Exception("Case not found")
    private val charCode = testChar.code

    @Test
    fun crypto_encryptValueAndIncreaseKeyIndex_indexIncreased() {
        val keyData = KeyData(TEST_KEY)
        charCode.crypto(
            case = charCase,
            keyData = keyData,
            mode = ObfustringCryptoMode.ENCRYPT
        )
        assert(keyData.index == 1)
    }

    @Test
    fun crypto_encryptValue_notEqualOriginal() {
        val keyData = KeyData(TEST_KEY)
        val result =
            charCode.crypto(
                case = charCase,
                keyData = keyData,
                mode = ObfustringCryptoMode.ENCRYPT
            )
        assert(result != testChar)
    }

    @Test
    fun crypto_decryptValue_equalOriginal() {
        val encrypted =
            charCode.crypto(
                case = charCase,
                keyData = KeyData(TEST_KEY),
                mode = ObfustringCryptoMode.ENCRYPT
            )

        val encryptedCharCode = encrypted.code
        val encryptedCharCase = encrypted.alphabeticCase() ?: throw Exception("Case not found")
        val decrypted =
            encryptedCharCode.crypto(
                case = encryptedCharCase,
                keyData = KeyData(TEST_KEY),
                mode = ObfustringCryptoMode.DECRYPT
            )
        assert(decrypted == testChar)
    }

    companion object {
        const val TEST_KEY = "CryptoTest"
    }
}