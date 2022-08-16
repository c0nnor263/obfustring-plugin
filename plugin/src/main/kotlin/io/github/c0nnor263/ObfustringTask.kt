package io.github.c0nnor263

import io.github.c0nnor263.obfustring_core.ObfStr
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import java.io.File


abstract class ObfustringTask : DefaultTask() {

    @Input
    var packageKey: String = "abc"

    @TaskAction
    fun obfustring() {
        (project.extensions.getByName("kotlin") as KotlinAndroidProjectExtension)
            .sourceSets.getByName("main").kotlin
            .filter { it.isFile && it.extension == "kt" }.forEach { file ->
                obfustringEncodeJavaFile(file, packageKey)
            }
    }


    private fun obfustringEncodeJavaFile(file: File, packageKey: String) {
        val fileText = file.readText()
        if (!fileText.contains(Templates().annotationTemp)) return

        val listOfFileLines = file.readLines().toMutableList()

        val pendingLogCheck: MutableList<(() -> Unit)?> = mutableListOf(
            {
                if (!fileText.contains(Templates().importTemp)) {
                    val index = listOfFileLines.indexOfFirst { it.contains("import ") }
                    if (index != -1) {
                        listOfFileLines.add(
                            index,
                            Templates().importTemp
                        )
                    } else {
                        listOfFileLines.add(
                            Templates().importTemp
                        )
                    }
                }
            }
        )

        val indexesOfClass = listOfFileLines.mapIndexed { index, line ->
            if (line.contains(Templates().annotationTemp)) index else -1
        }.filter { it != -1 }

        indexesOfClass.forEach { indexOfClass ->
            var leftBracketCount = 0
            var rightBracketCount = 0


            listOfFileLines.forEachIndexed { listIndex, line ->
                if (listIndex >= indexOfClass) {
                    val indexOfCurrentLine = listOfFileLines.indexOf(line)
                    val mapOfLines = mutableMapOf<Int, Int>()
                    val leftBracketsInLine = line.count { it == '{' }
                    val rightBracketsInLine = line.count { it == '}' }

                    if (leftBracketsInLine > 0) leftBracketCount += leftBracketsInLine
                    if (rightBracketsInLine > 0) rightBracketCount += rightBracketsInLine

                    if (checkLineForCompatibility(line)) return@forEachIndexed

                    // Check line for log existing
                    checkLineForLogExisting(line, listOfFileLines)?.let { pendingLogCheck.add(it) }

                    // Check for quote count
                    checkForQuoteCount(line) { firstOccurIndex, secondOccurIndex ->
                        mapOfLines[firstOccurIndex] = secondOccurIndex
                    }


                    var bufferLine = line
                    for ((firstIndex, secondIndex) in mapOfLines.toSortedMap(reverseOrder())) {
                        if (firstIndex == -1 || secondIndex == -1) {
                            continue
                        }

                        encodeValue(packageKey, bufferLine, firstIndex, secondIndex) { value ->
                            bufferLine = value
                        }
                    }

                    listOfFileLines[indexOfCurrentLine] = bufferLine

                    if (leftBracketCount > 0 &&
                        rightBracketCount > 0 &&
                        leftBracketCount == rightBracketCount
                    ) return@forEach
                }
            }
        }

        pendingLogCheck.reversed().forEach {
            it?.invoke()
        }
        pendingLogCheck.clear()

        File(file.absolutePath).writeText(listOfFileLines.joinToString("\n"))
    }

    private fun encodeValue(
        packageKey: String,
        line: String,
        firstIndex: Int,
        secondIndex: Int,
        callback: (String) -> Unit
    ) {
        val encoder = ObfStr(packageKey)
        val stringToEncode = line.substring(firstIndex, secondIndex + 1)

        val newValue = Templates().encodedValueTemp(packageKey, encoder, stringToEncode)
        callback(line.replace(stringToEncode, newValue))
    }


    private fun checkForQuoteCount(line: String, callback: (Int, Int) -> Unit) {
        val listOfQuotes = line.getListOfQuotes()
        listOfQuotes.forEachIndexed { index, char ->
            if ((index + 1) % 2 == 0) {
                callback(listOfQuotes[index - 1], char)
            }
        }
    }

    private fun checkLineForCompatibility(line: String): Boolean {
        val trimmedLine = line.trimStart()
        return line.contains(Templates().alreadyEncodedStringTemp) ||
                line.indexOfFirst { it == '"' } < line.indexOf("const va") ||
                trimmedLine.startsWith("//") ||
                trimmedLine.startsWith("/*") ||
                trimmedLine.startsWith("*/") ||
                (trimmedLine.startsWith('@') && line.contains("(")) ||
                trimmedLine.isBlank()

    }


    // Return lambda for future invoke
    private fun checkLineForLogExisting(
        lineForCheck: String,
        listOfFileLines: MutableList<String>
    ): (() -> Unit)? {
        val trimmedLine = lineForCheck.trimStart()

        return if (
            trimmedLine.startsWith("Log.") &&
            !trimmedLine.startsWith(Templates().alreadyEncodedLogTemp)
        ) {
            val indexOfCurrentLine = listOfFileLines.indexOf(lineForCheck)
            val stringBuilder = StringBuilder(lineForCheck)

            run searchBrackets@{
                listOfFileLines.forEachIndexed { index, checkLine ->
                    if (index >= indexOfCurrentLine) {
                        val closeLogBracket = checkLine.lastIndexOf(")")
                        if (closeLogBracket != -1) return@searchBrackets

                        // To paste whole log above obfuscated
                        stringBuilder.append(checkLine)
                    }
                }
            }


            return {
                val lineToPaste = Templates().encodedLogTemp(stringBuilder)
                listOfFileLines.run {
                    if (!get(indexOfCurrentLine - 1).contains(Templates().alreadyEncodedLogTemp) &&
                        none { it == lineToPaste }
                    ) {
                        add(indexOfCurrentLine, lineToPaste)
                    }
                }
            }
        } else null
    }


}