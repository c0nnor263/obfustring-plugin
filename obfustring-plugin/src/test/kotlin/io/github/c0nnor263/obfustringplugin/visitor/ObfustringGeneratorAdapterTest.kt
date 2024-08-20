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

import io.github.c0nnor263.obfustringcore.Obfustring
import io.github.c0nnor263.obfustringcore.ObfustringCryptoMode
import io.github.c0nnor263.obfustringplugin.ObfustringPlugin
import io.github.c0nnor263.obfustringplugin.log.ObfustringLogger
import io.github.c0nnor263.obfustringplugin.model.ClassVisitorParams
import io.github.c0nnor263.obfustringplugin.visitor.ObfustringGeneratorAdapter.Companion.EXCEPTION_MISMATCHED_STRING
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.provider.Property
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertThrows
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@MockKExtension.ConfirmVerification
@MockKExtension.CheckUnnecessaryStub
@TestInstance(Lifecycle.PER_CLASS)
class ObfustringGeneratorAdapterTest {
    private lateinit var parameters: Property<ObfustringVisitorFactory.InstrumentationParams>

    @BeforeEach
    fun setup() {
        parameters =
            mockk {
                every { get().mode.get() } returns io.github.c0nnor263.obfustringplugin.enums.ObfustringMode.DEFAULT
                every { get().key.get() } returns "key"
            }
        ObfustringPlugin.pluginExtension =
            mockk {
                every { excludeClasses } returns emptyList()
                every { customObfustring } returns Obfustring
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
    fun visitLdcInsn_constantIsString_superVisitLdcInsnNeverCalled() {
        val methodVisitor = mockk<MethodVisitor>()
        every {
            methodVisitor.visitFieldInsn(any(), any(), any(), any())
            methodVisitor.visitLdcInsn(any())
            methodVisitor.visitInsn(any())
            methodVisitor.visitMethodInsn(any(), any(), any(), any(), any())
        } answers {}

        val visitorParams = ClassVisitorParams(parameters.get())
        val obfustringGeneratorAdapter =
            ObfustringGeneratorAdapter(
                visitorParams,
                Opcodes.ASM9,
                methodVisitor,
                1,
                "test_name",
                "()Ljava/lang/String;"
            )

        obfustringGeneratorAdapter.visitLdcInsn("test")
        verify(inverse = true) {
            methodVisitor.visitLdcInsn("test")
        }
        verify(atLeast = 1) {
            ObfustringPlugin.logger.quiet(any())
        }
    }

    @Test
    fun visitLdcInsn_constantIsNotString_superVisitLdcInsnCalled() {
        val methodVisitor = mockk<MethodVisitor>()
        every {
            methodVisitor.visitFieldInsn(any(), any(), any(), any())
            methodVisitor.visitLdcInsn(10)
            methodVisitor.visitInsn(any())
            methodVisitor.visitMethodInsn(any(), any(), any(), any(), any())
        } answers {}

        val visitorParams = ClassVisitorParams(parameters.get())
        val obfustringGeneratorAdapter =
            ObfustringGeneratorAdapter(
                visitorParams,
                Opcodes.ASM9,
                methodVisitor,
                1,
                "test_name",
                "()Ljava/lang/String;"
            )

        obfustringGeneratorAdapter.visitLdcInsn(10)
        verify(atLeast = 1) {
            methodVisitor.visitLdcInsn(10)
        }
        verify(inverse = true) {
            ObfustringPlugin.logger.quiet(any())
        }
    }

    @Test
    fun visitLdcInsn_constantIsEmptyString_superVisitLdcInsnCalled() {
        val methodVisitor = mockk<MethodVisitor>()
        every {
            methodVisitor.visitFieldInsn(any(), any(), any(), any())
            methodVisitor.visitLdcInsn("")
            methodVisitor.visitInsn(any())
            methodVisitor.visitMethodInsn(any(), any(), any(), any(), any())
        } answers {}

        val visitorParams = ClassVisitorParams(parameters.get())
        val obfustringGeneratorAdapter =
            ObfustringGeneratorAdapter(
                visitorParams,
                Opcodes.ASM9,
                methodVisitor,
                1,
                "test_name",
                "()Ljava/lang/String;"
            )

        obfustringGeneratorAdapter.visitLdcInsn("")
        verify(atLeast = 1) {
            methodVisitor.visitLdcInsn("")
        }
        verify(inverse = true) {
            ObfustringPlugin.logger.quiet(any())
        }
    }

    @Test
    fun replaceStringWithDeobfuscationMethod_encodedAndDecodedValuesNotMatch_throwException() {
        val methodVisitor = mockk<MethodVisitor>()
        every {
            methodVisitor.visitFieldInsn(any(), any(), any(), any())
            methodVisitor.visitLdcInsn(any())
            methodVisitor.visitInsn(any())
            methodVisitor.visitMethodInsn(any(), any(), any(), any(), any())
        } answers {}

        every {
            ObfustringPlugin.pluginExtension.customObfustring.process(any(), any(), ObfustringCryptoMode.ENCRYPT)
        } answers { "" }

        every {
            ObfustringPlugin.pluginExtension.customObfustring.process(any(), any(), ObfustringCryptoMode.DECRYPT)
        } returns "randomValue"

        val visitorParams = ClassVisitorParams(parameters.get())
        val obfustringGeneratorAdapter =
            ObfustringGeneratorAdapter(
                visitorParams,
                Opcodes.ASM9,
                methodVisitor,
                1,
                "test_name",
                "()Ljava/lang/String;"
            )

        val exception =
            assertThrows<IllegalArgumentException> {
                obfustringGeneratorAdapter.visitLdcInsn("test")
            }

        assert(exception.message == EXCEPTION_MISMATCHED_STRING("test", "randomValue"))
        verify(inverse = true) {
            methodVisitor.visitLdcInsn("test")
            ObfustringPlugin.logger.quiet(any())
        }
    }
}