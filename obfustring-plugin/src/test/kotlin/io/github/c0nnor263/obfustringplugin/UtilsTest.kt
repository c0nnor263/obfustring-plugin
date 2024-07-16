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

import io.github.c0nnor263.obfustringcore.CommonObfustring
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class UtilsTest {
    @Test
    fun generateRandomKey_generateKey_returnsUniqueKey() {
        val firstResult = generateRandomKey()
        val secondResult = generateRandomKey()
        assert(firstResult != secondResult)
    }

    @Test
    fun verifyKey_keyWithDotsAndWhiteSpaces_returnsCorrectKey() {
        val key = "key.with.dots and white spaces"
        val result = verifyKey(key)
        assert(result.contains('.').not())
        assert(result.contains(' ').not())
    }

    @Test
    fun checkCustomObfustring_passObfustringClass_throwsIllegalStateException() {
        val exception =
            assertThrows<IllegalArgumentException> {
                checkCustomObfustring(MockClassObfustringTest())
            }
        assert(exception.message == ObfustringPlugin.EXCEPTION_INVALID_CUSTOM_OBFUSTRING)
    }

    @Test
    fun checkCustomObfustring_passObfustringObject_returnObfustring() {
        val result =
            assertDoesNotThrow {
                checkCustomObfustring(MockObjectObfustringTest)
            }
        assert(result is MockObjectObfustringTest)
    }
}

class MockClassObfustringTest : CommonObfustring {
    override fun process(
        key: String,
        stringValue: String,
        mode: Int
    ): String {
        return ""
    }
}

object MockObjectObfustringTest : CommonObfustring {
    override fun process(
        key: String,
        stringValue: String,
        mode: Int
    ): String {
        return ""
    }
}