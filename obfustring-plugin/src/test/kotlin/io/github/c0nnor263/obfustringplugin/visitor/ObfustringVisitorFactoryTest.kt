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

package io.github.c0nnor263.obfustringplugin.visitor

import com.android.build.api.instrumentation.InstrumentationContext
import io.github.c0nnor263.obfustringcore.annotations.ObfustringThis
import io.github.c0nnor263.obfustringplugin.ObfustringPlugin
import io.github.c0nnor263.obfustringplugin.log.ObfustringLogger
import io.github.c0nnor263.obfustringplugin.model.ExcludedClassInfo
import io.github.c0nnor263.obfustringplugin.model.createEmptyClassData
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlin.test.assertTrue
import org.gradle.api.provider.Property
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@MockKExtension.ConfirmVerification
@MockKExtension.CheckUnnecessaryStub
@TestInstance(Lifecycle.PER_CLASS)
class ObfustringVisitorFactoryTest {
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
    fun isInstrumentable_passExcludedClassData_returnsFalse() {
        val classData =
            createEmptyClassData().copy(
                className = "com.example.Test",
                classAnnotations = listOf(ObfustringThis::class.java.name)
            )
        every {
            ObfustringPlugin.pluginExtension.excludeClasses
        } returns
            listOf(
                ExcludedClassInfo(
                    name = "com.example.Test"
                )
            )
        val factory = MockObfustringVisitorFactoryTest()
        val result = factory.isInstrumentable(classData)
        assertFalse(result)
    }

    @Test
    fun isInstrumentable_passNotExcludedClassData_returnsTrue() {
        val classData =
            createEmptyClassData().copy(
                className = "com.example.Test",
                classAnnotations = listOf(ObfustringThis::class.java.name)
            )
        every {
            ObfustringPlugin.pluginExtension.excludeClasses
        } returns listOf()
        val factory = MockObfustringVisitorFactoryTest()
        val result = factory.isInstrumentable(classData)
        assertTrue(result)
    }

    @Test
    fun isInstrumentable_passNotExcludedClassDataWithList_returnsTrue() {
        val classData =
            createEmptyClassData().copy(
                className = "com.example.Test",
                classAnnotations = listOf(ObfustringThis::class.java.name)
            )
        every {
            ObfustringPlugin.pluginExtension.excludeClasses
        } returns
            listOf(
                ExcludedClassInfo(
                    name = "com.example.Test1"
                ),
                ExcludedClassInfo(
                    name = "com.example.Test1",
                    annotations =
                        listOf(
                            ExcludedClassInfo(
                                name = "com.example.Test2"
                            )
                        )
                )
            )
        val factory = MockObfustringVisitorFactoryTest()
        val result = factory.isInstrumentable(classData)
        assertTrue(result)
    }
}

internal class MockObfustringVisitorFactoryTest : ObfustringVisitorFactory() {
    override val instrumentationContext: InstrumentationContext
        get() = mockk()
    override val parameters: Property<InstrumentationParams>
        get() = mockk()
}