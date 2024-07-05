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

package io.github.c0nnor263.obfustringplugin.enums

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ObfustringModeTest {

    @Test
    fun isEnabled_passDisabled_returnsFalse() {
        val result = ObfustringMode.DISABLED.isEnabled()
        assertFalse(result)
    }

    @Test
    fun isEnabled_passDefault_returnsTrue() {
        val result = ObfustringMode.DEFAULT.isEnabled()
        assertTrue(result)
    }

    @Test
    fun isEnabled_passForce_returnsTrue() {
        val result = ObfustringMode.FORCE.isEnabled()
        assertTrue(result)
    }
}