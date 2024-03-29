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

import com.joom.grip.mirrors.getObjectType
import com.joom.grip.mirrors.toAsmType
import io.github.c0nnor263.obfustringcore.Obfustring
import io.github.c0nnor263.obfustringcore.ObfustringCryptoMode
import io.github.c0nnor263.obfustringplugin.model.ClassVisitorParams
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

internal class ObfustringClassVisitor(
    private val params: ClassVisitorParams,
    nextClassVisitor: ClassVisitor
) : ClassVisitor(Opcodes.ASM9, nextClassVisitor) {
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return ObfustringGeneratorAdapter(
            params = params,
            api = api,
            methodVisitor = visitor,
            access = access,
            name = name,
            descriptor = descriptor
        )
    }
}

internal class ObfustringGeneratorAdapter(
    private val params: ClassVisitorParams,
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String
) : GeneratorAdapter(api, methodVisitor, access, name, descriptor) {
    companion object {
        private val stringType = Type.getType(String::class.java)
        private val obfustringType = getObjectType(Obfustring::class.java)

        private val processMethod =
            Method(
                Obfustring::class.java.methods.find { it.name == "process" }?.name ?: "process",
                Type.getType(String::class.java),
                arrayOf(stringType, stringType, Type.INT_TYPE)
            )
    }

    override fun visitLdcInsn(constant: Any) {
        if (constant is String && constant.isNotBlank()) {
            replaceStringWithDeobfuscationMethod(constant)
        } else {
            super.visitLdcInsn(constant)
        }
    }

    private fun replaceStringWithDeobfuscationMethod(string: String) {
        val (key, isLoggingEnabled) = params
        val encodedString = Obfustring.process(key, string, ObfustringCryptoMode.ENCRYPT)
        val decodedString = Obfustring.process(key, encodedString, ObfustringCryptoMode.DECRYPT)
        require(decodedString == string) {
            "${Obfustring::class.java.simpleName} | Error: Decoded string [$decodedString] does not match input string [$string]"
        }

        push(key)
        push(encodedString)
        push(ObfustringCryptoMode.DECRYPT)
        invokeStatic(obfustringType.toAsmType(), processMethod)

        if (isLoggingEnabled) {
            println(
                "\t\t- FOUND: [$string]\n" +
                    "\t\t\tENCODED: [$encodedString]\n" +
                        "\t\t\tORIGINAL: [$decodedString]"
            )
        }
    }
}