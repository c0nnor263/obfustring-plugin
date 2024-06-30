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

import io.github.c0nnor263.obfustringcore.enums.AlphabeticCase
import io.github.c0nnor263.obfustringcore.model.KeyData
import org.junit.jupiter.api.Test

class UtilsTest {

    @Test
    fun tryProcess_encryptAndDecryptChar_encryptAndDecryptEquals() {
        val char = 'A'
        val encryptKeyData = KeyData(TEST_KEY)
        val decryptKeyData = KeyData(TEST_KEY)

        val encryptedChar = char.tryProcess(encryptKeyData, ObfustringCryptoMode.ENCRYPT)
        val decryptedChar = encryptedChar?.tryProcess(decryptKeyData, ObfustringCryptoMode.DECRYPT)
        assert(char == decryptedChar)
    }

    @Test
    fun tryProcess_tryEncryptZeroChar_returnNull() {
        val keyData = KeyData(TEST_KEY)
        val char = '0'
        val result = char.tryProcess(keyData, ObfustringCryptoMode.ENCRYPT)
        assert(result == null)
    }

    @Test
    fun tryProcess_tryDecryptEscapeSymbolChar_returnNull() {
        val keyData = KeyData(TEST_KEY)
        val char = '\\'
        val result = char.tryProcess(keyData, ObfustringCryptoMode.DECRYPT)
        assert(result == null)
    }

    @Test
    fun alphabeticCase_getCaseFromUpperCharCase_returnUpperCase() {
        val char = 'A'
        val result = char.alphabeticCase()
        assert(result == AlphabeticCase.UPPER_CASE)
    }

    @Test
    fun alphabeticCase_getCaseFromLowerCharCase_returnLowerCase() {
        val char = 'a'
        val result = char.alphabeticCase()
        assert(result == AlphabeticCase.LOWER_CASE)
    }

    @Test
    fun alphabeticCase_getCaseFromZeroChar_returnNull() {
        val char = '0'
        val result = char.alphabeticCase()
        assert(result == null)
    }

    @Test
    fun alphabeticCase_getCaseFromEscapeSymbol_returnNull() {
        val keyData = KeyData(TEST_KEY)
        val char = '\\'
        val result = char.tryProcess(keyData, ObfustringCryptoMode.DECRYPT)
        assert(result == null)
    }

    companion object {
        const val TEST_KEY = "UtilsTest"
    }
}