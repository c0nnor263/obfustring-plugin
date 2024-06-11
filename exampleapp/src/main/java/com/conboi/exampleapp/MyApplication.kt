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

package com.conboi.exampleapp

import android.app.Application
import android.util.Log
import io.github.c0nnor263.obfustringcore.ObfustringThis
import kotlin.random.Random

@ObfustringThis
class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
        val username = "user#${Random.nextInt()}"
        val onCreateMsg = "Hello world and $username!"
    }

    override fun onCreate() {
        super.onCreate()
        val userChecker = UserChecker()
        val isValidUserMsg =
            if (userChecker.isValidName(username)) {
                onCreateMsg
            } else {
                "$username is not valid user name"
            }

        Log.i(
            TAG,
            "Application onCreate: $isValidUserMsg"
        )
    }
}

@Suppress("DEPRECATION")
@ObfustringThis
class UserChecker {
    companion object {
        @Deprecated("This is a deprecated list")
        private val forbiddenNames = listOf("admin", "root", "user")
    }

    fun isValidName(name: String): Boolean {
        return when {
            name.isBlank() -> false
            name.isEmpty() -> false
            forbiddenNames.contains(name) -> false
            else -> true
        }.also { result ->
            Log.i(
                "TAG",
                "\tisValidName: $name is $result\n" +
                    "\tAll forbidden names: $forbiddenNames"
            )
        }
    }
}