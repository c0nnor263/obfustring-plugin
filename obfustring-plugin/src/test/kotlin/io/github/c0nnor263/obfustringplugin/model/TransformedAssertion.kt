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

import org.junit.jupiter.api.Assertions.assertTrue

data class TransformedAssertion(
    val className: String,
    val methodName: String,
    val methodInsn: String,
) {
    var nameAsserted = false
    var methodNameAsserted = false
    var methodInsnAsserted = false

    fun assertNameAtClass(visitedClassName: String?) {
        if (nameAsserted) return
        nameAsserted = visitedClassName == className
    }

    fun assertMethodNameAtClass(className: String?, methodName: String?) {
        if (methodNameAsserted) return
        if (className == this.className) {
            methodNameAsserted = "$methodName()Ljava/lang/String;" == this.methodName
        }
    }

    fun assertMethodInsnAtClass(className: String?, owner: String?, methodName: String?) {
        if (methodInsnAsserted) return
        if (className == this.className) {
            methodInsnAsserted =
                "$owner.$methodName(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;" == methodInsn
        }
    }

    fun finalAssert() {
        assertTrue(nameAsserted)
        assertTrue(methodNameAsserted)
        assertTrue(methodInsnAsserted)
    }
}