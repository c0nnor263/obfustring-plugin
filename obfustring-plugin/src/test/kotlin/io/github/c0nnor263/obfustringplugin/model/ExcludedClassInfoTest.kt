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

import com.android.build.gradle.internal.instrumentation.ClassDataImpl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExcludedClassInfoTest {
    @Nested
    inner class Name {
        @Test
        fun checkIfExcluded_nameExcluded_returnsTrue() {
            val excludedName = "com.example.ExcludedClass"
            val excludedInfo = ExcludedClassInfo(name = excludedName)
            val classData = createEmptyClassData().copy(className = excludedName)

            val result = excludedInfo.checkIfExcluded(classData)
            assert(result)
        }

        @Test
        fun checkIfExcluded_nameNotExcluded_returnsFalse() {
            val excludedName = "com.example.ExcludedClass"
            val excludedInfo = ExcludedClassInfo(name = excludedName)
            val classData = createEmptyClassData().copy(className = "com.example.NotExcludedClass")

            val result = excludedInfo.checkIfExcluded(classData)
            assert(!result)
        }
    }

    @Nested
    inner class Prefix {
        @Test
        fun checkIfExcluded_prefixNameExcluded_returnsTrue() {
            val excludedClassPrefixName = "com.example.Excluded"
            val excludedInfo = ExcludedClassInfo(prefixName = excludedClassPrefixName)
            val classData = createEmptyClassData().copy(className = excludedClassPrefixName + "Class")

            val result = excludedInfo.checkIfExcluded(classData)
            assert(result)
        }

        @Test
        fun checkIfExcluded_prefixNameNotExcluded_returnsFalse() {
            val excludedClassPrefixName = "com.example.Excluded"
            val excludedInfo = ExcludedClassInfo(prefixName = excludedClassPrefixName)
            val classData = createEmptyClassData().copy(className = "com.example.NotExcludedClass")

            val result = excludedInfo.checkIfExcluded(classData)
            assert(!result)
        }
    }

    @Nested
    inner class Suffix {
        @Test
        fun checkIfExcluded_suffixNameExcluded_returnsTrue() {
            val excludedClassSuffixName = "ExcludedClass"
            val excludedInfo = ExcludedClassInfo(suffixName = excludedClassSuffixName)
            val classData = createEmptyClassData().copy(className = "com.example.$excludedClassSuffixName")

            val result = excludedInfo.checkIfExcluded(classData)
            assert(result)
        }

        @Test
        fun checkIfExcluded_suffixNameNotExcluded_returnsFalse() {
            val excludedClassSuffixName = "NotExcludedClassAwesome"
            val excludedInfo = ExcludedClassInfo(suffixName = excludedClassSuffixName)
            val classData = createEmptyClassData().copy(className = "com.example.NotExcludedClass")

            val result = excludedInfo.checkIfExcluded(classData)
            assert(!result)
        }
    }

    @Nested
    inner class Annotation {
        @Test
        fun checkIfExcluded_annotationExcluded_returnsTrue() {
            val excludedClassAnnotation = "com.example.ExcludedAnnotation"
            val excludedInfo =
                ExcludedClassInfo(
                    annotations =
                        listOf(
                            ExcludedClassInfo(excludedClassAnnotation)
                        )
                )
            val classData =
                createEmptyClassData().copy(
                    classAnnotations = listOf(excludedClassAnnotation)
                )

            val result = excludedInfo.checkIfExcluded(classData)
            assert(result)
        }

        @Test
        fun checkIfExcluded_annotationNotExcluded_returnsFalse() {
            val excludedClassAnnotation = "com.example.ExcludedAnnotation"
            val excludedInfo =
                ExcludedClassInfo(
                    annotations =
                        listOf(
                            ExcludedClassInfo(excludedClassAnnotation)
                        )
                )
            val classData =
                createEmptyClassData().copy(
                    classAnnotations = listOf("com.example.NotExcludedAnnotation")
                )

            val result = excludedInfo.checkIfExcluded(classData)
            assert(!result)
        }

        @Test
        fun checkIfExcluded_multipleAnnotationsExcluded_returnsTrue() {
            val excludedFirstAnnotation = "com.example.ExcludedAnnotation1"
            val excludedSecondAnnotation = "com.example.ExcludedAnnotation2"
            val excludedInfo =
                ExcludedClassInfo(
                    annotations =
                        listOf(
                            ExcludedClassInfo(excludedFirstAnnotation),
                            ExcludedClassInfo(excludedSecondAnnotation)
                        )
                )
            val classData =
                createEmptyClassData().copy(
                    classAnnotations =
                        listOf(
                            excludedFirstAnnotation,
                            excludedSecondAnnotation,
                            "com.example.NotExcludedAnnotation"
                        )
                )
            val result = excludedInfo.checkIfExcluded(classData)
            assert(result)
        }

        @Test
        fun checkIfExcluded_withoutAnnotations_returnsFalse() {
            val excludedClassAnnotation = "com.example.ExcludedAnnotation"
            val excludedInfo =
                ExcludedClassInfo(
                    annotations =
                        listOf(
                            ExcludedClassInfo(excludedClassAnnotation)
                        )
                )
            val classData =
                createEmptyClassData().copy(
                    classAnnotations = emptyList()
                )

            val result = excludedInfo.checkIfExcluded(classData)
            assert(!result)
        }
    }
}

fun createEmptyClassData(): ClassDataImpl {
    return ClassDataImpl(
        "",
        emptyList(),
        emptyList(),
        emptyList()
    )
}