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

package com.conboi.exampleapp

import android.app.Application
import android.util.Log
import io.github.c0nnor263.obfustring.core.ObfustringThis
import kotlin.random.Random

@ObfustringThis
class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
        val user = "user#${Random.nextInt()}"
        val onCreateMsg = "Hello world and $user!"
    }

    override fun onCreate() {
        super.onCreate()
        val userChecker = UserChecker(user)
        Log.i(
            TAG,
            "Application onCreate: ${
                if (userChecker.isValidName()) onCreateMsg else "User is not valid"
            }"
        )
    }
}

@ObfustringThis
class UserChecker(private val name: String) {
    companion object {
        @Deprecated("This is a deprecated list")
        val forbiddenNames = listOf("admin", "root", "user")
    }

    fun isValidName(): Boolean {
        return name.isNotBlank() && name.isNotEmpty() && !forbiddenNames.contains(name).also {
            Log.i("TAG", "isValidName: $it $name $forbiddenNames")
        }
    }
}
