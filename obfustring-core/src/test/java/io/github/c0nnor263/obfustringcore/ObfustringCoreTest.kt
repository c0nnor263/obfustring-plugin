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

import io.github.c0nnor263.obfustringcore.Obfustring.EMPTY_KEY_MSG
import io.github.c0nnor263.obfustringcore.enums.AlphabeticCase
import io.github.c0nnor263.obfustringcore.model.Crypto.Companion.crypto
import io.github.c0nnor263.obfustringcore.model.KeyData
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ObfustringCoreTest {
    companion object {
        const val TEST_KEY = "test_obfustring_key"
    }

    @Nested
    inner class Common {
        @Test
        fun emptyKey_exceptionWithMessage() {
            val encryptException =
                assertThrows<IllegalArgumentException> {
                    Obfustring.process(
                        key = "",
                        stringValue = "test",
                        mode = ObfustringCryptoMode.ENCRYPT
                    )
                }
            val decryptException =
                assertThrows<IllegalArgumentException> {
                    Obfustring.process(
                        key = "",
                        stringValue = "test",
                        mode = ObfustringCryptoMode.DECRYPT
                    )
                }
            assert(encryptException.message == EMPTY_KEY_MSG)
            assert(decryptException.message == EMPTY_KEY_MSG)
        }
    }

    @Nested
    inner class Processing {
        @Test
        fun simpleStringEncryption_originalAndDecryptedEquals() {
            val originalString = "Hello World, this is a test string"

            val encryptedString =
                Obfustring.process(
                    key = TEST_KEY,
                    stringValue = originalString,
                    mode = ObfustringCryptoMode.ENCRYPT
                )

            val decryptedString =
                Obfustring.process(
                    key = TEST_KEY,
                    stringValue = encryptedString,
                    mode = ObfustringCryptoMode.DECRYPT
                )

            assert(originalString == decryptedString)
        }

        @Test
        fun complexStringEncryption_originalAndDecryptedEquals() {
            val strings =
                listOf(
                    " !Hola World, this is a test string with numbers 1234567890 and special characters !@#$%^&*()_+",
                    "&Greetings World \n",
                    "@This is a string with escape characters: \\t (tab), \\n (newline), \\\" (double quote)",
                    "Unicode characters: \u03A9 (Greek Omega), \u2602 (Umbrella), \uD83D\uDE00 (Grinning Face)",
                    "#Special symbols: ♥♦♣♠",
                    "%Math symbols: ∑ ∫ π",
                    "*Programming-related symbols: { } [ ] ( ) < >",
                    "~Currency symbols: $ € ¥ £",
                    "+Emoticons: 😊 😎 🤔",
                    "^And many more strange symbols: ༼ つ ◕_◕ ༽つ ㋛ ٩(◕‿◕｡)۶",
                    " ¡Chinese characters: 你好世界 (Hello World), 愛 (Love), 福 (Luck)",
                    "¥Japanese characters: こんにちは (Konnichiwa), ありがとう (Arigatou), さようなら (Sayonara)",
                    "@Russian charact\r\r\r\r\n\ters: Привет мир (Privet Mir - Hello World), Любовь (Love), " +
                            "Счастье (Happiness)",
                    "&Arabic characters: السلام عليكم (As-salamu alaykum - Peace be upon you), حب (Love), " +
                            "سعادة (Happiness)",
                    "#Symbols from different cultures: ☯ 卍 ☪",
                    "%Astrological symbols: ♈ ♉ ♊ ♋ ♌ ♍ ♎ ♏ ♐ ♑ ♒ ♓",
                    "&Weather symbols: ☀ ☁ ⚡ ☔ ❄",
                    "+Chess pieces: ♔ ♕ ♖ ♗ ♘ ♙ ♚ ♛ ♜ ♝ ♞ ♟",
                    "^Geometric shapes: ◆ ◇ ◈ □ ▢ ▣ ▤ ▥ ▦ ▧ ▨ ▩",
                    "!Music symbols: ♪ ♫ ♬ ♭ ♮ ♯",
                    "@Transportation symbols: 🚗 🚲 ✈️ 🚀 🛸",
                    "*Animal symbols: 🐶 🐱 🐼 🐯 🦁 🐰",
                    "#Food and drink symbols: 🍎 🍕 🍣 🍺 🍹 🍩",
                    "~Fancy arrows: ➡️ ⬅️ ⬆️ ⬇️ ↗️ ↘️ ↙️ ↖️",
                    "¡Playing cards: 🂠 🂡 🂢 🂣 🂤 🂥 🂦 🂧 🂨 🂩 🂪 🂫",
                    "¥Miscellaneous symbols: ☢ ☣ ⚛ ⚕ ⚖ ✇",
                    "^More emoji faces: 😜 😇 😍 🙄 🤯 🥳",
                    "!Artistic symbols: ★ ☆ ♢ ♤ ♧ ♡ ❖ ✦ ✧",
                    "&Communication symbols: ☎ ✉ ☏ ✒ ✎ ✏",
                    "@Time-related symbols: ⌛ ⏳ ⏰ ⌚ ⏱ ⏲",
                    "*Zodiac signs: ♈ ♉ ♊ ♋ ♌ ♍ ♎ ♏ ♐ ♑ ♒ ♓",
                    "~Nature symbols: 🌲 🌸 🍂 🌊 🌈 ☀️",
                    "+Technology symbols: 💻 📱 🖥 🕹 📷 📡",
                    "^Fantasy symbols: 🧙‍♂️ 🐉 🧚‍♀️ 🏰 🌌 🚀",
                    "!Medical symbols: ⚕ 🩹 🌡 🏥 🩺 💊",
                    "¥Symbols of luck: 🍀 🎰 🤞 🐞 🌈 🎐",
                    "#Enigmatic Ensembles: ✪ ❂ ☣ ☯ ∞",
                    "%Linguistic Labyrinths: ꜜ ꜝ ꜞ ꜟ",
                    "*Timeless Tales: ∮ ∯ ∰ ∱",
                    "@Whimsical Wanderings: ⚘ ⚑ ⚛ ⚜",
                    "&Quantum Quirks: ℏ Ω ℧ ℜ ℑ",
                    "^Ethereal Escapades: ⌬ ⌭ ⌮ ⌯",
                    "!Mystical Musings: ☽ ☾ ☿ ♁",
                    "¡Celestial Chronicles: ☄ ☪ ☭ ☮",
                    "¥Nebulous Narratives: ☁ ☂ ☃ ☠",
                    "~Arcane Adventures: ☩ ☪ ☫ ☬",
                    "+Enchanting Episodes: ♛ ♜ ♝ ♞",
                    "@Puzzling Parables: ♆ ♇ ♈ ♉",
                    "*Whimsical Wonders: ✿ ❀ ❁ ❃"
                )

            strings.forEach { originalString ->
                val encryptedString =
                    Obfustring.process(
                        key = TEST_KEY,
                        stringValue = originalString,
                        mode = ObfustringCryptoMode.ENCRYPT
                    )
                val decryptedString =
                    Obfustring.process(
                        key = TEST_KEY,
                        stringValue = encryptedString,
                        mode = ObfustringCryptoMode.DECRYPT
                    )
                assert(originalString == decryptedString)
            }
        }

        @Test
        fun cryptoKeyDataIndex_IndexIncreased() {
            val initialIndex = 0
            val keyData = KeyData(TEST_KEY, index = initialIndex)
            val char = 'A'
            val case = char.alphabeticCase()!!
            val code = char.code
            code.crypto(
                case = case,
                keyData = keyData,
                mode = ObfustringCryptoMode.ENCRYPT
            )
            assert(keyData.index == initialIndex + 1)
        }

        @Test
        fun cryptoEncryptValue_notEqualOriginal() {
            val keyData = KeyData(TEST_KEY)
            val char = 'A'
            val case = char.alphabeticCase()!!
            val code = char.code
            val result =
                code.crypto(
                    case = case,
                    keyData = keyData,
                    mode = ObfustringCryptoMode.ENCRYPT
                )
            assert(result != char)
        }

        @Test
        fun cryptoDecryptValue_equalOriginal() {
            val original = 'A'
            val encrypted =
                original.code.crypto(
                    case = original.alphabeticCase()!!,
                    keyData = KeyData(TEST_KEY),
                    mode = ObfustringCryptoMode.ENCRYPT
                )
            val decrypted =
                encrypted.code.crypto(
                    case = encrypted.alphabeticCase()!!,
                    keyData = KeyData(TEST_KEY),
                    mode = ObfustringCryptoMode.DECRYPT
                )
            assert(decrypted == original)
        }
    }

    @Nested
    inner class CharOperations {
        @Test
        fun tryProcessEncrypt_returnNull() {
            val keyData = KeyData(TEST_KEY)
            val char = '0'
            val result = char.tryProcess(keyData, ObfustringCryptoMode.ENCRYPT)
            assert(result == null)
        }

        @Test
        fun tryProcessDecrypt_returnNull() {
            val keyData = KeyData(TEST_KEY)
            val char = '\\'
            val result = char.tryProcess(keyData, ObfustringCryptoMode.DECRYPT)
            assert(result == null)
        }

        @Test
        fun alphabeticCase_returnUpperCase() {
            val char = 'A'
            val result = char.alphabeticCase()
            assert(result == AlphabeticCase.UPPER_CASE)
        }

        @Test
        fun alphabeticCase_returnLowerCase() {
            val char = 'a'
            val result = char.alphabeticCase()
            assert(result == AlphabeticCase.LOWER_CASE)
        }

        @Test
        fun alphabeticCase_returnNull() {
            val char = '0'
            val result = char.alphabeticCase()
            assert(result == null)
        }
    }
}