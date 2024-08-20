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

import io.github.c0nnor263.obfustringcore.annotations.ObfustringThis
import io.github.c0nnor263.obfustringplugin.ObfustringPlugin
import io.github.c0nnor263.obfustringplugin.enums.ObfustringMode
import io.github.c0nnor263.obfustringplugin.log.ObfustringLogger
import io.github.c0nnor263.obfustringplugin.model.ClassDataHandler
import io.github.c0nnor263.obfustringplugin.model.ClassVisitorParams
import io.github.c0nnor263.obfustringplugin.model.createEmptyClassData
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlin.test.assertFalse
import org.gradle.api.provider.Property
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.objectweb.asm.ClassVisitor

@MockKExtension.ConfirmVerification
@MockKExtension.CheckUnnecessaryStub
@TestInstance(Lifecycle.PER_CLASS)
class ObfustringClassVisitorTest {
    private lateinit var ownerClassSet: MutableSet<String>
    private lateinit var parameters: Property<ObfustringVisitorFactory.InstrumentationParams>

    @BeforeEach
    fun setup() {
        parameters =
            mockk {
                every { get().mode.get() } returns ObfustringMode.DEFAULT
                every { get().key.get() } returns "key"
            }
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

        ownerClassSet = mutableSetOf()
    }

    @Test
    fun visit_passInstrumentableClassData_isInstrumentable() {
        val classVisitor = mockk<ClassVisitor>()
        every {
            classVisitor.visit(1, 1, "TEST", null, "TEST", null)
        } answers {}

        val visitorParams = ClassVisitorParams(parameters.get())
        val classData =
            createEmptyClassData().copy(
                className = "com.example.Test",
                classAnnotations = listOf(ObfustringThis::class.java.name)
            )
        val classDataHandler = ClassDataHandler(classData)
        val verifyClassAdapter = VerifyClassAdapter(classVisitor)
        val obfustringVisitor =
            ObfustringClassVisitor(
                visitorParams = visitorParams,
                classDataHandler = classDataHandler,
                nextClassVisitor = verifyClassAdapter,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className -> null }
            )
        obfustringVisitor.visit(1, 1, "TEST", null, "TEST", null)
        assertTrue(visitorParams.isInstrumentable)
    }

    @Test
    fun visitInnerClass_passInstrumentableClassData_ownerClassSetContainsOwner() {
        val classVisitor = mockk<ClassVisitor>()
        every {
            classVisitor.visit(1, 1, "TEST", null, "TEST", null)
            classVisitor.visitInnerClass("com/example/Test\$Inner", "com/example/Test", "Inner", 1)
        } answers {}

        val visitorParams = ClassVisitorParams(parameters.get())
        val classData =
            createEmptyClassData().copy(
                className = "com.example.Test",
                classAnnotations = listOf(ObfustringThis::class.java.name)
            )
        val classDataHandler = ClassDataHandler(classData)
        val verifyClassAdapter = VerifyClassAdapter(classVisitor)
        val obfustringVisitor =
            ObfustringClassVisitor(
                visitorParams = visitorParams,
                classDataHandler = classDataHandler,
                nextClassVisitor = verifyClassAdapter,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className -> null }
            )

        obfustringVisitor.visit(1, 1, "TEST", null, "TEST", null)
        obfustringVisitor.visitInnerClass("com/example/Test\$Inner", "com/example/Test", "Inner", 1)
        assertTrue(ownerClassSet.contains("com/example/Test"))
    }

    @Test
    fun visitInnerClass_passNotInstrumentableClassData_ownerClassSetNotContainsOwner() {
        val classVisitor = mockk<ClassVisitor>()
        every {
            classVisitor.visit(1, 1, "TEST", null, "TEST", null)
            classVisitor.visitInnerClass("com/example/Test\$Inner", "com/example/Test", "Inner", 1)
        } answers {}

        val visitorParams = ClassVisitorParams(parameters.get())
        val classData =
            createEmptyClassData().copy(
                className = "com.example.Test"
            )
        val classDataHandler = ClassDataHandler(classData)
        val verifyClassAdapter = VerifyClassAdapter(classVisitor)
        val obfustringVisitor =
            ObfustringClassVisitor(
                visitorParams = visitorParams,
                classDataHandler = classDataHandler,
                nextClassVisitor = verifyClassAdapter,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className -> null }
            )

        obfustringVisitor.visit(1, 1, "TEST", null, "TEST", null)
        obfustringVisitor.visitInnerClass("com/example/Test\$Inner", "com/example/Test", "Inner", 1)
        assertFalse(ownerClassSet.contains("com/example/Test"))
    }

    @Test
    fun visitOuterClass_passNotInstrumentableInnerClassData_innerClassIsInstrumentableByOwnerClassSet() {
        val classVisitorOwner = mockk<ClassVisitor>()
        val classVisitorInner = mockk<ClassVisitor>()
        every {
            classVisitorOwner.visit(1, 1, "TEST", null, "TEST", null)
            classVisitorOwner.visitInnerClass("com/example/Test\$Inner", "com/example/Test", "Inner", 1)

            classVisitorInner.visit(1, 1, "TEST", null, "TEST", null)
            classVisitorInner.visitOuterClass("com/example/Test", "Test", null)
        } answers {}

        val visitorParamsOwner = ClassVisitorParams(parameters.get())
        val visitorParamsInner = ClassVisitorParams(parameters.get())
        val classDataOwner =
            createEmptyClassData().copy(
                className = "com.example.Test",
                classAnnotations = listOf(ObfustringThis::class.java.name)
            )
        val classDataHandlerOwner = ClassDataHandler(classDataOwner)
        val verifyClassAdapterOwner = VerifyClassAdapter(classVisitorOwner)
        val obfustringVisitorOwner =
            ObfustringClassVisitor(
                visitorParams = visitorParamsOwner,
                classDataHandler = classDataHandlerOwner,
                nextClassVisitor = verifyClassAdapterOwner,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className -> null }
            )

        val verifyClassAdapterInner = VerifyClassAdapter(classVisitorInner)
        val classDataHandlerInner =
            ClassDataHandler(
                createEmptyClassData().copy(
                    className = "com.example.Test\$Inner"
                )
            )
        val obfustringVisitorInner =
            ObfustringClassVisitor(
                visitorParams = visitorParamsInner,
                classDataHandler = classDataHandlerInner,
                nextClassVisitor = verifyClassAdapterInner,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className -> null }
            )

        obfustringVisitorOwner.visit(1, 1, "TEST", null, "TEST", null)
        obfustringVisitorOwner.visitInnerClass("com/example/Test\$Inner", "com/example/Test", "Inner", 1)

        obfustringVisitorInner.visit(1, 1, "TEST", null, "TEST", null)
        obfustringVisitorInner.visitOuterClass("com/example/Test", "Test", null)

        assertTrue(ownerClassSet.contains("com/example/Test"))
        assertTrue(visitorParamsOwner.isInstrumentable)
        assertTrue(visitorParamsInner.isInstrumentable)
    }

    @Test
    fun visitOuterClass_passNotInstrumentableInnerClassData_innerClassIsInstrumentableByOwnerClassDataHandler() {
        val classVisitorOwner = mockk<ClassVisitor>()
        val classVisitorInner = mockk<ClassVisitor>()
        every {
            classVisitorOwner.visit(1, 1, "TEST", null, "TEST", null)
            classVisitorOwner.visitInnerClass("com/example/Test\$Inner", "com/example/Test", "Inner", 1)

            classVisitorInner.visit(1, 1, "TEST", null, "TEST", null)
            classVisitorInner.visitOuterClass("com/example/Test", "Test", null)
        } answers {}

        val visitorParamsOwner = ClassVisitorParams(parameters.get())
        val visitorParamsInner = ClassVisitorParams(parameters.get())
        val classDataOwner =
            createEmptyClassData().copy(
                className = "com.example.Test",
                classAnnotations = listOf(ObfustringThis::class.java.name)
            )
        val classDataHandlerOwner = ClassDataHandler(classDataOwner)
        val verifyClassAdapterOwner = VerifyClassAdapter(classVisitorOwner)
        val obfustringVisitorOwner =
            ObfustringClassVisitor(
                visitorParams = visitorParamsOwner,
                classDataHandler = classDataHandlerOwner,
                nextClassVisitor = verifyClassAdapterOwner,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className -> null }
            )

        val verifyClassAdapterInner = VerifyClassAdapter(classVisitorInner)
        val classDataHandlerInner =
            ClassDataHandler(
                createEmptyClassData().copy(
                    className = "com.example.Test\$Inner"
                )
            )
        val obfustringVisitorInner =
            ObfustringClassVisitor(
                visitorParams = visitorParamsInner,
                classDataHandler = classDataHandlerInner,
                nextClassVisitor = verifyClassAdapterInner,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className ->
                    val classData =
                        createEmptyClassData().copy(
                            className = className,
                            classAnnotations =
                                listOf(
                                    ObfustringThis::class.java.name
                                )
                        )
                    ClassDataHandler(classData)
                }
            )

        obfustringVisitorOwner.visit(1, 1, "TEST", null, "TEST", null)
        obfustringVisitorInner.visit(1, 1, "TEST", null, "TEST", null)
        obfustringVisitorInner.visitOuterClass("com/example/Test", "Test", null)

        assertTrue(ownerClassSet.isEmpty())
        assertTrue(visitorParamsOwner.isInstrumentable)
        assertTrue(visitorParamsInner.isInstrumentable)
    }

    @Test
    fun visitOuterClass_passNotInstrumentableInnerClassData_innerClassIsNotInstrumentableByOwnerClassDataHandler() {
        val classVisitorOwner = mockk<ClassVisitor>()
        val classVisitorInner = mockk<ClassVisitor>()
        every {
            classVisitorOwner.visit(1, 1, "TEST", null, "TEST", null)
            classVisitorOwner.visitInnerClass("com/example/Test\$Inner", "com/example/Test", "Inner", 1)

            classVisitorInner.visit(1, 1, "TEST", null, "TEST", null)
            classVisitorInner.visitOuterClass("com/example/Test", "Test", null)
        } answers {}

        val visitorParamsOwner = ClassVisitorParams(parameters.get())
        val visitorParamsInner = ClassVisitorParams(parameters.get())
        val classDataOwner =
            createEmptyClassData().copy(
                className = "com.example.Test"
            )
        val classDataHandlerOwner = ClassDataHandler(classDataOwner)
        val verifyClassAdapterOwner = VerifyClassAdapter(classVisitorOwner)
        val obfustringVisitorOwner =
            ObfustringClassVisitor(
                visitorParams = visitorParamsOwner,
                classDataHandler = classDataHandlerOwner,
                nextClassVisitor = verifyClassAdapterOwner,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className -> null }
            )

        val verifyClassAdapterInner = VerifyClassAdapter(classVisitorInner)
        val classDataHandlerInner =
            ClassDataHandler(
                createEmptyClassData().copy(
                    className = "com.example.Test\$Inner"
                )
            )
        val obfustringVisitorInner =
            ObfustringClassVisitor(
                visitorParams = visitorParamsInner,
                classDataHandler = classDataHandlerInner,
                nextClassVisitor = verifyClassAdapterInner,
                ownerClassSet = ownerClassSet,
                onCreateClassDataHandler = { className ->
                    val classData =
                        createEmptyClassData().copy(
                            className = className
                        )
                    ClassDataHandler(classData)
                }
            )

        obfustringVisitorOwner.visit(1, 1, "TEST", null, "TEST", null)
        obfustringVisitorInner.visit(1, 1, "TEST", null, "TEST", null)
        obfustringVisitorInner.visitOuterClass("com/example/Test", "Test", null)

        assertTrue(ownerClassSet.isEmpty())
        assertFalse(visitorParamsOwner.isInstrumentable)
        assertFalse(visitorParamsInner.isInstrumentable)
    }
}