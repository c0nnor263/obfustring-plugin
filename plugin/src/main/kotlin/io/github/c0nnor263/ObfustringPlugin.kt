package io.github.c0nnor263

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import io.github.c0nnor263.obfustring_core.ObfustringEncoder
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


class ObfustringPlugin : Plugin<Project> {
    private var isAlreadyStarted = false
    override fun apply(project: Project) {
        project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            .onVariants { variant ->
                if (!isAlreadyStarted) {
                    println("> Obfustring")
                    val packageName = variant.applicationId.get()
                    beginWork(project, packageName)
                    isAlreadyStarted = true
                }
            }
    }

    private fun beginWork(project: Project, packageName: String) {
        val packageKey = packageName.filter { it != '.' }
        val encoder = ObfustringEncoder(packageKey)
        val classesTree =
            project.fileTree("src/main/java").filter { it.isFile && it.extension == "kt" }.files
        classesTree.forEach processClasses@{ file ->
            processFile(encoder, file, packageKey)
        }
    }

    private fun processFile(encoder: ObfustringEncoder, file: File, packageKey: String) {
        if (!file.readText().contains("@Obfustring")) return

        val list = file.readLines().toMutableList()

        val pendingLogCheck: MutableList<(() -> Unit)?> = mutableListOf(
            {
                if (!file.readText().contains("import io.github.c0nnor263.obfustring_core.ObfustringEncoder")
                ) {
                    list.add(2, "import io.github.c0nnor263.obfustring_core.ObfustringEncoder")
                }
            }
        )

        val classIndexes = list.mapIndexed { index, line ->
            if (line.contains("@Obfustring")) index else -1
        }.filter { it != -1 }

        classIndexes.forEach { classIndex ->
            var leftBracketCount = 0
            var rightBracketCount = 0
            list.forEachIndexed { listIndex, line ->
                if (listIndex >= classIndex) {
                    val leftBracketInLineCount = line.count { it == '{' }
                    val rightBracketInLineCount = line.count { it == '}' }

                    if (leftBracketInLineCount > 0) leftBracketCount += leftBracketInLineCount
                    if (rightBracketInLineCount > 0) rightBracketCount += rightBracketInLineCount

                    if (checkLineForCompatibility(line)) return@forEachIndexed

                    checkLineForLog(line, list)?.let { pendingLogCheck.add(it) }

                    val index = list.indexOf(line)
                    val lineMap = mutableMapOf<Int, Int>()

                    var firstOccurIndex = -1
                    var secondOccurIndex = -1
                    var quotesOccurCount = 0

                    line.forEachIndexed loopLine@{ indexC: Int, c: Char ->
                        val checkEscapeSymbolIndex = if (indexC - 1 >= 0) indexC - 1 else 0
                        if (line[checkEscapeSymbolIndex] == '\\') return@loopLine
                        if (c == '"') {
                            when (quotesOccurCount) {
                                0 -> firstOccurIndex = indexC
                                1 -> secondOccurIndex = indexC
                            }
                            quotesOccurCount++
                            if (quotesOccurCount > 1) {
                                lineMap[firstOccurIndex] = secondOccurIndex
                                quotesOccurCount = 0
                                return@loopLine
                            }
                        }
                    }

                    for ((firstIndex, secondIndex) in lineMap) {
                        if (firstIndex == -1 || secondIndex == -1) {
                            continue
                        }


                            val foundString = line.substring(firstIndex, secondIndex + 1)

                            val newValue =
                                "${ObfustringEncoder::class.simpleName}(\"$packageKey\")" +
                                        ".${ObfustringEncoder::vigenere.name}(${
                                            encoder.vigenere(foundString, true)
                                        })"

                            val edit = line.replace(foundString, newValue)
                            list[index] = edit
                    }
                    if (leftBracketCount > 0 && rightBracketCount > 0) {
                        if (leftBracketCount == rightBracketCount) return@forEach
                    }

                }
            }
        }

        pendingLogCheck.reversed().forEach {
            it?.invoke()
        }
        pendingLogCheck.clear()
        println("> Processing $file")
        File(file.absolutePath).writeText(list.joinToString("\n"))
    }

    private fun checkLineForCompatibility(line: String): Boolean {
        return line.contains(
            "ObfustringEncoder(\""
        ) ||
                line.indexOfFirst { it == '"' } < line.indexOf("const va") ||
                (line.trimStart().startsWith('@') && line.contains("(")) ||
                line.trimStart().startsWith("//")
    }

    private fun checkLineForLog(line: String, list: MutableList<String>): (() -> Unit)? {
        if (line.trimStart().startsWith("Log.")) {
            val startBracket = list.indexOf(line)
            val sb = StringBuilder(line)
            run searchBracket@{
                list.forEachIndexed { index, checkLine ->
                    if (index >= startBracket) {
                        val closeBracket = checkLine.lastIndexOf(")")
                        if (closeBracket != -1) return@searchBracket
                        sb.append(checkLine)
                    }
                }
            }
            val currentLineIndex = list.indexOf(line)
            return {
                val newLine = "//@ | ${sb.trimStart()}"
                if (list.any { it != newLine }) {
                    if (list[currentLineIndex - 1].contains("//@")) {
                        list.removeAt(currentLineIndex - 1)
                    }
                    list.add(currentLineIndex, newLine)
                }
            }
        }
        return null
    }
}