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

import io.github.c0nnor263.obfustringplugin.ObfustringPlugin
import io.github.c0nnor263.obfustringplugin.model.ClassDataHandler
import io.github.c0nnor263.obfustringplugin.model.ClassVisitorParams
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal class ObfustringClassVisitor(
    private val visitorParams: ClassVisitorParams,
    private val classDataHandler: ClassDataHandler,
    private val ownerClassSet: MutableSet<String>,
    private val onCreateClassDataHandler: (String) -> ClassDataHandler?,
    nextClassVisitor: ClassVisitor
) : ClassVisitor(Opcodes.ASM9, nextClassVisitor) {
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        classDataHandler.checkIfInstrumentable(visitorParams)
        if (visitorParams.isInstrumentable) {
            ObfustringPlugin.logger.quiet(LOG_INFO_INSTRUMENTABLE(classDataHandler.name))
        }
    }

    override fun visitInnerClass(
        name: String?,
        outerName: String?,
        innerName: String?,
        access: Int
    ) {
        super.visitInnerClass(name, outerName, innerName, access)
        if (visitorParams.isInstrumentable) {
            val className = classDataHandler.getQualifiedName()
            ownerClassSet.add(className)
        }
    }

    override fun visitOuterClass(
        owner: String?,
        name: String?,
        descriptor: String?
    ) {
        super.visitOuterClass(owner, name, descriptor)
        if (owner == null) {
            return
        }

        if (ownerClassSet.contains(owner)) {
            visitorParams.isInstrumentable = true
            ObfustringPlugin.logger.quiet(LOG_INFO_INSTRUMENTABLE_INNER(owner, classDataHandler.name))
        } else {
            val ownerQualifiedName = owner.replace("/", ".")
            val ownerClassDataHandler = onCreateClassDataHandler(ownerQualifiedName)
            ownerClassDataHandler?.checkIfInstrumentable(visitorParams)
            if (visitorParams.isInstrumentable) {
                ObfustringPlugin.logger.quiet(LOG_INFO_INSTRUMENTABLE_INNER(owner, classDataHandler.name))
            }
        }
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (!visitorParams.isInstrumentable) {
            return visitor
        }
        return ObfustringGeneratorAdapter(
            params = visitorParams,
            api = api,
            methodVisitor = visitor,
            access = access,
            name = name,
            descriptor = descriptor
        )
    }

    companion object {
        val LOG_INFO_INSTRUMENTABLE: (String) -> String = { className ->
            "\n\t- CLASS: $className"
        }
        val LOG_INFO_INSTRUMENTABLE_INNER: (String, String) -> String = { ownerClassName, innerClassName ->
            "\n\t- CLASS: $innerClassName\n\t  OWNER CLASS: $ownerClassName"
        }
    }
}