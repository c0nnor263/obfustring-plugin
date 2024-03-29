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

package io.github.c0nnor263.obfustringplugin.enums

enum class StringConcatStrategy(val rawArgument: String) {
    /**
     * Performs concatenation via StringConcatFactory.makeConcatWithConstants(...)
     * Most efficient strategy
     */
    INDY_WITH_CONSTANTS("-Xstring-concat=indy-with-constants"),

    /**
     *  Performs concatenation via StringConcatFactory.makeConcat(...)
     *  Required for Obfustring if you want to obfuscate all strings
     */
    INDY("-Xstring-concat=indy"),

    /**
     *  Performs classic concatenation via StringBuilder.append(...)
     *  Slowest strategy according to [INDY] and [INDY_WITH_CONSTANTS]
     */
    INLINE("-Xstring-concat=inline")
}