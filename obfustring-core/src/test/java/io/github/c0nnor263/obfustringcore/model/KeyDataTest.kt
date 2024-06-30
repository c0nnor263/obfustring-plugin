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

import org.junit.jupiter.api.Test

class KeyDataTest {

    @Test
    fun increase_increaseKeyData_indexIncreased() {
        val keyData = KeyData(TEST_KEY)
        keyData.increase()
        assert(keyData.index == 1)
    }

    @Test
    fun increase_indexBiggerThanKeyLength_indexEqualsZero() {
        val keyData = KeyData(TEST_KEY)
        repeat(TEST_KEY.length) {
            keyData.increase()
        }
        assert(keyData.index == 0)
    }

    @Test
    fun getCode_getCharCodeFromKey_returnsCode() {
        val keyData = KeyData(TEST_KEY)
        assert(keyData.getCode() == TEST_KEY[0].code)
    }

    @Test
    fun getCode_getCharCodeFromKeyByIncreasedIndex_returnsCodeByIncreasedIndex() {
        val keyData = KeyData(TEST_KEY)
        keyData.increase()
        assert(keyData.getCode() == TEST_KEY[1].code)
    }

    @Test
    fun getCode_getCharCodeFromKeyAfterResetKeyIndex_returnsCodeByResetIndex() {
        val keyData = KeyData(TEST_KEY)
        repeat(TEST_KEY.length) {
            keyData.increase()
        }
        assert(keyData.getCode() == TEST_KEY[0].code)
    }

    companion object {
        const val TEST_KEY = "KeyDataTest"
    }
}