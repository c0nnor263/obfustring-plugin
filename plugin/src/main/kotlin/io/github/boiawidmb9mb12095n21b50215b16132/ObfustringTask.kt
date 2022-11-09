package io.github.boiawidmb9mb12095n21b50215b16132

import io.github.boiawidmb9mb12095n21b50215b16132.b21nm01om5n1905mw0bdkb2b515.ObfStr
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import java.io.File


abstract class ObfustringTask : DefaultTask() {
	@get:Input
	abstract val key: Property<String>

	@TaskAction
	fun obfustring() {
		val formatKey = key.get().map {
			if (!it.isLetter()) {
				(('A'..'Z') + ('a'..'z')).random()
			} else it
		}.joinToString("")

		val kFiles =
			(project.extensions.getByName("kotlin") as KotlinAndroidProjectExtension).sourceSets.getByName(
				"main"
			).kotlin.filter { it.isFile && it.extension == "kt" }

		val encoder = ObfStr(formatKey)

		kFiles.forEach { file ->
			obfustringEncodeSourceFile(file, encoder)
		}
	}

	private fun obfustringEncodeSourceFile(file: File, encoder: ObfStr) {
		val fileText = file.readText()
		if (!fileText.contains(Templates().obfustringThisAnnotationTemp)) return

		val listOfFileLines = file.readLines().toMutableList()

		val pendingLogCheck: MutableList<(() -> Unit)?> = mutableListOf({
			val template = Templates().importTemp(
				encoder.javaClass.`package`.name,
				encoder.javaClass.simpleName
			)
			if (!fileText.contains(template)) {
				val index = listOfFileLines.indexOfFirst { it.contains("import ") }
				if (index != -1) {
					listOfFileLines.add(
						index, template
					)
				} else {
					listOfFileLines.add(
						template
					)
				}
			}
		})

		val indexesOfClass = listOfFileLines.mapIndexed { index, line ->
			if (line.contains(Templates().obfustringThisAnnotationTemp)) index else -1
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

					val previousIndex = if (listIndex - 1 < 0) listIndex else listIndex - 1
					val nextIndex =
						if (listOfFileLines.size - 1 < listIndex + 1) listIndex else listIndex + 1
					if (checkLineForCompatibility(
							line = line,
							previousLine = listOfFileLines[previousIndex],
							nextLine = listOfFileLines[nextIndex]
						)
					) return@forEachIndexed

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

						encodeValue(
							encoder = encoder, bufferLine, firstIndex, secondIndex
						) { value ->
							bufferLine = value
						}
					}

					listOfFileLines[indexOfCurrentLine] = bufferLine

					if (leftBracketCount > 0 && rightBracketCount > 0 && leftBracketCount == rightBracketCount) return@forEach
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
		encoder: ObfStr, line: String, firstIndex: Int, secondIndex: Int, callback: (String) -> Unit
	) {

		val stringToEncode = line.substring(firstIndex, secondIndex + 1)

		val countOfStringsToEncode = line.split(stringToEncode).size - 1
		val newValue = Templates().encodedValueTemp(encoder, stringToEncode)

		if (countOfStringsToEncode > 1) {
			callback(line.replaceLast(stringToEncode, newValue))
		} else {
			callback(line.replace(stringToEncode, newValue))
		}
	}

	private fun String.replaceLast(
		delimiter: String, replacement: String, missingDelimiterValue: String = this
	): String {
		val index = lastIndexOf(delimiter)
		val lastSymbol = index + delimiter.lastIndex + 1
		return if (index == -1) missingDelimiterValue else replaceRange(
			index, lastSymbol, replacement
		)
	}

	private fun checkForQuoteCount(line: String, callback: (Int, Int) -> Unit) {
		val listOfQuotes = line.getListOfQuotes()
		listOfQuotes.forEachIndexed { index, char ->
			if ((index + 1) % 2 == 0) {
				callback(listOfQuotes[index - 1], char)
			}
		}
	}

	private fun checkLineForCompatibility(
		line: String, previousLine: String, nextLine: String
	): Boolean {
		val trimmedLine = line.trimStart()
		val trimmedPreviousLine = previousLine.trimStart()
		val trimmedNextLine = nextLine.trimStart()
		return line.contains(Templates().alreadyEncodedStringTemp) || trimmedLine.startsWith("const val") || (trimmedLine.startsWith(
			'\"'
		) && trimmedPreviousLine.startsWith("const val")) || (trimmedLine.startsWith('\"') && trimmedPreviousLine.startsWith(
			"private const val"
		)) || (trimmedLine.startsWith("const val") && trimmedNextLine.startsWith('\"')) || (trimmedLine.startsWith(
			"private const val"
		) && trimmedNextLine.startsWith('\"')) || trimmedLine.startsWith("private const val") || trimmedLine.startsWith(
			"//"
		) || trimmedLine.startsWith("/*") || trimmedLine.contains("*/") || trimmedLine.contains('¦') || (trimmedLine.startsWith(
			'@'
		) && line.contains("(")) || trimmedLine.isBlank()

	}


	// Return lambda for future invoke
	private fun checkLineForLogExisting(
		lineForCheck: String, listOfFileLines: MutableList<String>
	): (() -> Unit)? {
		val trimmedLine = lineForCheck.trimStart()

		return if (trimmedLine.startsWith("Log.") && !trimmedLine.startsWith(Templates().alreadyEncodedLogTemp)) {
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
					if (!get(indexOfCurrentLine - 1).contains(Templates().alreadyEncodedLogTemp) && none { it == lineToPaste }) {
						add(indexOfCurrentLine, lineToPaste)
					}
				}
			}
		} else null
	}
}