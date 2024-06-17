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

import com.android.build.api.instrumentation.ClassData

data class ExcludedClassInfo(
    val name: String? = null,
    val prefixName: String? = null,
    val suffixName: String? = null,
    val annotations: List<ExcludedClassInfo>? = null
) {

    // TODO: Test this function
    fun checkIfExcluded(classData: ClassData): Boolean {
        return checkIfExcludedByName(classData.className) || classData.classAnnotations.any { annotation ->
            checkIfExcludedByAnnotation(annotation)
        }
    }

    private fun checkIfExcludedByAnnotation(annotation: String): Boolean {
        return annotations?.any { excludedAnnotation ->
            excludedAnnotation.checkIfExcludedByName(annotation)
        } ?: false
    }

    private fun ExcludedClassInfo.checkIfExcludedByName(className: String): Boolean {
        return when {
            name != null -> className == name
            prefixName != null -> className.startsWith(prefixName)
            suffixName != null -> className.endsWith(suffixName)
            else -> false
        }
    }
}
