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

package io.github.c0nnor263.obfustringplugin

import io.github.c0nnor263.obfustringcore.Obfustring
import io.github.c0nnor263.obfustringplugin.enums.ObfustringMode
import io.github.c0nnor263.obfustringplugin.enums.StringConcatStrategy
import io.github.c0nnor263.obfustringplugin.model.TransformedAssertion
import java.io.File
import org.gradle.api.Project
import org.gradle.internal.impldep.org.hamcrest.MatcherAssert.assertThat
import org.gradle.internal.impldep.org.hamcrest.core.IsNull
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertTimeout
import org.junit.jupiter.api.io.TempDir
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@TestInstance(Lifecycle.PER_CLASS)
class ObfustringPluginTest {
    companion object {
        @TempDir
        var testProjectDir: File? = null

        lateinit var dummyProject: Project
        lateinit var buildGradleFile: File
        var originalBuildGradle: File? = null

        var defaultRunner: GradleRunner = GradleRunner.create()

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            repeat(3) {
                try {
                    dummyProject = ProjectBuilder.builder().withProjectDir(File("test-project")).build()
                } catch (e: Exception) {
                    println("Error: $e")
                }
            }
            // Copy template test project to temp directory
            dummyProject.rootDir.copyRecursively(testProjectDir!!, overwrite = true)
            originalBuildGradle = dummyProject.rootDir.listFiles()?.find { it.name == "build.gradle.kts" }
            dummyProject.pluginManager.apply {
                apply("com.android.application")
                apply("kotlin-android")
                ObfustringPlugin().apply(dummyProject)
            }

            buildGradleFile = File(testProjectDir, "build.gradle.kts")
            defaultRunner = GradleRunner.create().withProjectDir(testProjectDir).withPluginClasspath()
        }
    }

    @BeforeEach
    fun beforeEach() {
        originalBuildGradle?.copyRecursively(buildGradleFile, overwrite = true)
    }


    @Nested
    inner class Initialization {

        @Test
        fun initPlugin_obfustringExtensionCreated() {
            val extension = dummyProject.extensions.getByType(ObfustringExtension::class.java)
            assertThat(extension, IsNull.notNullValue())
        }

        @Test
        fun initPlugin_obfustringCoreDependencyAdded() {
            val dependency = dummyProject.configurations.getByName("implementation").dependencies.find {
                it.name == "obfustring-core"
            }
            assertThat(dependency, IsNull.notNullValue())
        }
    }


    @Nested
    inner class Extension {

        @Test
        fun obfustringModeDisabled_stringConcatStrategyNotPresented() {
            val extension = dummyProject.extensions.getByType(ObfustringExtension::class.java)
            extension.apply {
                stringConcatStrategy = StringConcatStrategy.INDY
                mode = ObfustringMode.DISABLED
            }
            dummyProject.tasks.withType(KotlinJvmCompile::class.java).configureEach { task ->
                task.compilerOptions {
                    assert(freeCompilerArgs.get().isEmpty())
                }
            }
        }

        @Test
        fun obfustringModeDefault_stringConcatStrategyPresented() = with(dummyProject) {
            val concatStrategy = StringConcatStrategy.INLINE
            extensions.getByType(ObfustringExtension::class.java).run {
                stringConcatStrategy = concatStrategy
                mode = ObfustringMode.DEFAULT
            }
            tasks.withType(KotlinJvmCompile::class.java).configureEach { task ->
                task.compilerOptions {
                    assert(freeCompilerArgs.get().any { it == concatStrategy.rawArgument })
                }
            }
        }

        @Test
        fun setLoggingEnabled_loggingPresented() {
            buildGradleFile.appendText(
                """
                    
                    obfustring {
                        loggingEnabled = true
                    }
                """.trimIndent()
            )

            val result = runCheckTask()
            assert(result.task(":check")?.outcome == TaskOutcome.SUCCESS)
            assert(result.output.contains("${Obfustring.NAME} | KEY: "))
        }

        @Test
        fun setLoggingDisabled_loggingNotPresented() {
            buildGradleFile.appendText(
                """
                    
                    object CustomObfustring : CommonObfustring{
                         override fun process(
                            key: String,
                            stringValue: String,
                            mode: Int
                         ): String {
                            return stringValue
                         }
                    }
                    obfustring {
                        loggingEnabled = false
                        customObfustring = CustomObfustring
                    }
                """.trimIndent()
            )

            val result = runCheckTask()
            assert(result.task(":transformReleaseClassesWithAsm")?.outcome == TaskOutcome.SUCCESS)
            assert(result.output.contains(Obfustring.NAME).not())
        }
    }

    @Test
    fun usingDefaultObfustring_notLoggingCustom() {
        val result = runCheckTask()
        assert(result.task(":check")?.outcome == TaskOutcome.SUCCESS)
        assert(result.output.contains("${Obfustring.NAME} | CUSTOM_OBFUSTRING:").not())
    }

    @Test
    fun setCustomObfustring_loggingCustomObfustring() {
        buildGradleFile.appendText(
            """
                    
                    object CustomObfustring : CommonObfustring{
                         override fun process(
                            key: String,
                            stringValue: String,
                            mode: Int
                         ): String {
                            return stringValue
                         }
                    }
                    obfustring {
                        customObfustring = CustomObfustring
                    }
                """.trimIndent()
        )

        val result = runCheckTask()
        assert(result.task(":check")?.outcome == TaskOutcome.SUCCESS)
        assert(result.output.contains("${Obfustring.NAME} | CUSTOM_OBFUSTRING: CustomObfustring"))

    }


    @Nested
    inner class Obfuscation {
        @Test
        fun transformReleaseClassesWithAsm_classFieldHasObfustringMethod() {
            appendFileWithText(
                "src/main/java/com/test/MyApplication.kt",
                """
                    package com.test 
                    
                    import io.github.c0nnor263.obfustringcore.annotations.ObfustringThis
                    
                    @ObfustringThis
                    class MyApplication {
                        val password = "password"
                    }
                """.trimIndent()
            )

            val transformedAssertion = TransformedAssertion(
                className = "com/test/MyApplication",
                methodName = "getPassword()Ljava/lang/String;",
                methodInsn = "io/github/c0nnor263/obfustringcore/Obfustring.process(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;",
            )


            val result = runCheckTask()
            assert(result.task(":transformReleaseClassesWithAsm")?.outcome == TaskOutcome.SUCCESS)
            assertThatClassTransformed(transformedAssertion) { file ->
                val classReader = ClassReader(file.readBytes())
                val classVisitor = object : ClassVisitor(Opcodes.ASM9) {
                    var visitedClassName: String? = null
                    override fun visit(
                        version: Int,
                        access: Int,
                        name: String?,
                        signature: String?,
                        superName: String?,
                        interfaces: Array<out String>?
                    ) {
                        super.visit(version, access, name, signature, superName, interfaces)
                        visitedClassName = name
                        transformedAssertion.assertNameAtClass(visitedClassName)
                    }

                    override fun visitMethod(
                        access: Int,
                        name: String?,
                        descriptor: String?,
                        signature: String?,
                        exceptions: Array<out String>?
                    ): MethodVisitor? {
                        transformedAssertion.assertMethodNameAtClass(visitedClassName, name)
                        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)
                        return object : MethodVisitor(Opcodes.ASM9, visitor) {
                            override fun visitMethodInsn(
                                opcode: Int,
                                owner: String?,
                                name: String?,
                                descriptor: String?,
                                isInterface: Boolean
                            ) {
                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                                transformedAssertion.assertMethodInsnAtClass(visitedClassName, owner, name)
                            }
                        }
                    }
                }

                classReader.accept(classVisitor, 0)
            }
        }

        //        @Test
        // TODO: Fix this test
        fun transformReleaseClassesWithAsm_classHasObfustringMethod() {
            appendFileWithText(
                "src/main/java/com/test/MyApplication.kt",
                """
                    package com.test

                    import android.app.Application
                    import android.util.Log
                    import io.github.c0nnor263.obfustringcore.annotations.ObfustringThis
                    import kotlin.random.Random
                    
                    @ObfustringThis
                    class MyApplication : Application() {
                        companion object {
                            private const val TAG = "MyApplication"
                            val username = "user#${'$'}{Random.nextInt()}"
                            val onCreateMsg = "Hello world and ${'$'}username!"
                        }

                        override fun onCreate() {
                            super.onCreate()
                            val userChecker = UserChecker()
                            val isValidUserMsg =
                                if (userChecker.isValidName(username)) {
                                    onCreateMsg
                                } else {
                                    "${"$"}username is not valid user name"
                                }

                            Log.i(
                                TAG,
                                "Application onCreate: ${"$"}isValidUserMsg"
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
                                    "\tisValidName: ${"$"}name is ${"$"}result\n" +
                                            "\tAll forbidden names: ${"$"}forbiddenNames"
                                )
                            }
                        }
                    }
                """.trimIndent()
            )
            val transformedAssertion = TransformedAssertion(
                className = "com/test/MyApplication",
                methodName = "io/github/c0nnor263/obfustringcore/Obfustring.process(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;",
                methodInsn = "isValidName(Ljava/lang/String;)Z"
            )

            val result = runCheckTask()
            assert(result.task(":transformReleaseClassesWithAsm")?.outcome == TaskOutcome.SUCCESS)
            assertThatClassTransformed(transformedAssertion) { file ->
            }
        }
    }

    fun runCheckTask(): BuildResult {
        return defaultRunner.withArguments("check").build().also {
            assert(it.task(":check")?.outcome == TaskOutcome.SUCCESS)
        }
    }

    fun appendFileWithText(path: String, text: String) {
        val file = File(testProjectDir, path)
        file.parentFile.mkdirs()
        file.writeText(text)
    }

    fun assertThatClassTransformed(transformedAssertion: TransformedAssertion, action: (File) -> Unit) {
        assertTimeout(java.time.Duration.of(1000, java.time.temporal.ChronoUnit.SECONDS)) {
            testProjectDir?.listFiles()
                ?.find { it.name == "build" }?.listFiles()
                ?.find { it.name == "intermediates" }?.listFiles()
                ?.find { it.name == "classes" }?.listFiles()
                ?.find { it.name == "release" }?.listFiles()
                ?.find { it.name == "transformReleaseClassesWithAsm" }?.listFiles()
                ?.find { it.name == "dirs" }?.listFiles()
                ?.find { it.name == "com" }?.listFiles()
                ?.find { it.name == "test" }?.listFiles()?.also {
                    it.forEach(action)
                    transformedAssertion.finalAssert()
                }
        }
    }
}