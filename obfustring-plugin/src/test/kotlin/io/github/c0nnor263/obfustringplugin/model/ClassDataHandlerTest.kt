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

package io.github.c0nnor263.obfustringplugin.model

import io.github.c0nnor263.obfustringcore.annotations.ObfustringExclude
import io.github.c0nnor263.obfustringcore.annotations.ObfustringThis
import io.github.c0nnor263.obfustringplugin.ObfustringPlugin
import io.github.c0nnor263.obfustringplugin.enums.ObfustringMode
import io.github.c0nnor263.obfustringplugin.log.ObfustringLogger
import io.github.c0nnor263.obfustringplugin.visitor.ObfustringVisitorFactory
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlin.test.assertFalse
import org.gradle.api.provider.Property
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MockKExtension.ConfirmVerification
@MockKExtension.CheckUnnecessaryStub
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClassDataHandlerTest {
    @BeforeEach
    fun setup() {
        ObfustringPlugin.pluginExtension =
            mockk {
                every { excludeClasses } returns emptyList()
            }
        ObfustringPlugin.logger =
            ObfustringLogger(
                loggingEnabled = true,
                defaultLogger =
                    mockk {
                        every { quiet(any()) } returns Unit
                        every { error(any()) } returns Unit
                    }
            )
    }

    @Test
    fun getQualifiedName_passClassName_returnsCorrectQualifiedName() {
        val classData =
            createEmptyClassData().copy(
                className = "com.example.Test"
            )
        val classDataHandler = ClassDataHandler(classData)
        val qualifiedName = classDataHandler.getQualifiedName()
        assertTrue(qualifiedName == "com/example/Test")
    }

    @Nested
    inner class Default {
        private lateinit var parameters: Property<ObfustringVisitorFactory.InstrumentationParams>

        @BeforeEach
        fun setup() {
            parameters =
                mockk {
                    every { get().mode.get() } returns ObfustringMode.DEFAULT
                    every { get().key.get() } returns "key"
                }
        }

        @Test
        fun checkIfInstrumentable_passClassDataWithObfustringThis_returnsTrue() {
            val classData =
                createEmptyClassData().copy(
                    className = "com.example.Test",
                    classAnnotations = listOf(ObfustringThis::class.java.name)
                )
            val classDataHandler = ClassDataHandler(classData)
            val classVisitorParams = ClassVisitorParams(params = parameters.get())
            classDataHandler.checkIfInstrumentable(classVisitorParams)
            assertTrue(classVisitorParams.isInstrumentable)
        }

        @Test
        fun checkIfInstrumentable_passClassDataWithoutObfustringThis_returnsFalse() {
            val classData =
                createEmptyClassData().copy(
                    className = "com.example.Test"
                )
            val classDataHandler = ClassDataHandler(classData)
            val classVisitorParams = ClassVisitorParams(params = parameters.get())
            classDataHandler.checkIfInstrumentable(classVisitorParams)
            assertFalse(classVisitorParams.isInstrumentable)
        }

        @Test
        fun checkIfInstrumentable_passClassDataWithObfustringExclude_returnsFalse() {
            val classData =
                createEmptyClassData().copy(
                    className = "com.example.Test",
                    classAnnotations =
                        listOf(
                            ObfustringThis::class.java.name,
                            ObfustringExclude::class.java.name
                        )
                )
            val classDataHandler = ClassDataHandler(classData)
            val classVisitorParams = ClassVisitorParams(params = parameters.get())
            classDataHandler.checkIfInstrumentable(classVisitorParams)
            assertFalse(classVisitorParams.isInstrumentable)
        }
    }

    @Nested
    inner class ForceMode {
        private lateinit var parameters: Property<ObfustringVisitorFactory.InstrumentationParams>

        @BeforeEach
        fun setup() {
            parameters =
                mockk {
                    every { get().mode.get() } returns ObfustringMode.FORCE
                    every { get().key.get() } returns "key"
                }
        }

        @Test
        fun checkIfInstrumentable_passClassDataWithoutObfustringThis_returnsTrue() {
            val classData =
                createEmptyClassData().copy(
                    className = "com.example.Test"
                )
            val classDataHandler = ClassDataHandler(classData)
            val classVisitorParams = ClassVisitorParams(params = parameters.get())
            classDataHandler.checkIfInstrumentable(classVisitorParams)
            assertTrue(classVisitorParams.isInstrumentable)
        }

        @Test
        fun checkIfInstrumentable_passClassDataWithObfustringExclude_returnsFalse() {
            val classData =
                createEmptyClassData().copy(
                    className = "com.example.Test",
                    classAnnotations =
                        listOf(
                            ObfustringThis::class.java.name,
                            ObfustringExclude::class.java.name
                        )
                )
            val classDataHandler = ClassDataHandler(classData)
            val classVisitorParams = ClassVisitorParams(params = parameters.get())
            classDataHandler.checkIfInstrumentable(classVisitorParams)
            assertFalse(classVisitorParams.isInstrumentable)
        }
    }

    @Nested
    inner class DisabledMode {
        private lateinit var parameters: Property<ObfustringVisitorFactory.InstrumentationParams>

        @BeforeEach
        fun setup() {
            parameters =
                mockk {
                    every { get().mode.get() } returns ObfustringMode.DISABLED
                    every { get().key.get() } returns "key"
                }
        }

        @Test
        fun checkIfInstrumentable_passClassDataWithoutObfustringThis_returnsFalse() {
            val classData =
                createEmptyClassData().copy(
                    className = "com.example.Test"
                )
            val classDataHandler = ClassDataHandler(classData)
            val classVisitorParams = ClassVisitorParams(params = parameters.get())
            classDataHandler.checkIfInstrumentable(classVisitorParams)
            assertFalse(classVisitorParams.isInstrumentable)
        }

        @Test
        fun checkIfInstrumentable_passClassDataWithObfustringThis_returnsFalse() {
            val classData =
                createEmptyClassData().copy(
                    className = "com.example.Test",
                    classAnnotations = listOf(ObfustringThis::class.java.name)
                )
            val classDataHandler = ClassDataHandler(classData)
            val classVisitorParams = ClassVisitorParams(params = parameters.get())
            classDataHandler.checkIfInstrumentable(classVisitorParams)
            assertFalse(classVisitorParams.isInstrumentable)
        }
    }
}