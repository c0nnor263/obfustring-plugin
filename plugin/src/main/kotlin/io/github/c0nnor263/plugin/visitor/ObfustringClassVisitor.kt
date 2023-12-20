/*
 * Copyright 2023 Oleh Boichuk
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

package io.github.c0nnor263.plugin.visitor

import com.joom.grip.mirrors.getObjectType
import com.joom.grip.mirrors.toAsmType
import io.github.c0nnor263.obfustring.core.Obfustring
import io.github.c0nnor263.obfustring.core.ObfustringMode
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

class ObfustringClassVisitor(
    private val key: String,
    private val isLoggingEnabled: Boolean,
    nextClassVisitor: ClassVisitor
) : ClassVisitor(Opcodes.ASM9, nextClassVisitor) {

    companion object {
        private val stringType = Type.getType(String::class.java)
        private val obfustringType = getObjectType(Obfustring::class.java)

        private val processMethod = Method(
            Obfustring::class.java.methods.find { it.name == "process" }?.name ?: "process",
            Type.getType(String::class.java),
            arrayOf(stringType, stringType, Type.INT_TYPE)
        )
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return object : GeneratorAdapter(api, visitor, access, name, descriptor) {
            override fun visitLdcInsn(constant: Any) {
                if (constant is String && constant.isNotBlank()) {
                    replaceStringWithDeobfuscationMethod(constant)
                } else {
                    super.visitLdcInsn(constant)
                }
            }

            private fun replaceStringWithDeobfuscationMethod(string: String) {
                val encodedString = Obfustring.process(key, string, ObfustringMode.ENCRYPT)
                push(key)
                push(encodedString)
                push(ObfustringMode.DECRYPT)
                invokeStatic(obfustringType.toAsmType(), processMethod)

                if (isLoggingEnabled) {
                    val originalString =
                        Obfustring.process(
                            key,
                            encodedString,
                            ObfustringMode.DECRYPT
                        )
                    println(
                        "\t\t- FOUND: $string\n" +
                            "\t\t\tENCODED: $encodedString\n" +
                            "\t\t\tORIGINAL: $originalString"
                    )
                }
            }
        }
    }
}
