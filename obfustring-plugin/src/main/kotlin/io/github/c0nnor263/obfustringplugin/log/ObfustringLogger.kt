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

package io.github.c0nnor263.obfustringplugin.log

import org.gradle.api.logging.Logger

internal class ObfustringLogger(
    private val loggingEnabled: Boolean,
    private val defaultLogger: Logger
) : Logger by defaultLogger {
    override fun quiet(msg: String?) {
        if (!loggingEnabled) return
        defaultLogger.quiet(msg)
    }

    override fun error(msg: String) {
        println("Error: $msg $loggingEnabled")
        if (!loggingEnabled) return
        defaultLogger.error(msg)
    }
}