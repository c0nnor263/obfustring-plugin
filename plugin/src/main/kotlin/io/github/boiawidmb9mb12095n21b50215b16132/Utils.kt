package io.github.boiawidmb9mb12095n21b50215b16132

import io.github.boiawidmb9mb12095n21b50215b16132.b21nm01om5n1905mw0bdkb2b515.ObfStr
import io.github.boiawidmb9mb12095n21b50215b16132.b21nm01om5n1905mw0bdkb2b515.ObfustringThis

internal class Templates {
	val alreadyEncodedLogTemp = "//@ | "
	val alreadyEncodedStringTemp = "${ObfStr::class.java.simpleName}(\""
	val obfustringThisAnnotationTemp = "@${ObfustringThis::class.java.simpleName}"
	val importTemp:(String,String) -> String = {packageString,simpleName ->
		val formatPackage = ObfStr::class.java.`package`.name
		"import $formatPackage.$simpleName"
	}

	val encodedValueTemp: (
		encoder: ObfStr,
		encodedString: String
	) -> String = { encoder: ObfStr, stringToEncode: String ->

		"${ObfStr::class.simpleName}(\"${encoder.key}\")." +
				"${ObfStr::v.name}(" +
				encoder.v(
					string = stringToEncode,
					encrypt = true
				) +
				")"
	}
	val encodedLogTemp: (sb: StringBuilder) -> String = { sb ->
		"//@ | ${sb.trimStart()}"
	}
}

internal fun CharSequence.getListOfQuotes(): List<Int> {
	val input = '"'
	val listForIndexes = mutableListOf<Int>()
	var skipSymbolsCount = 0
	var skipBracketCount = 0

	forEachIndexed { index, c ->
		if (skipSymbolsCount != 0) {
			skipSymbolsCount--
			return@forEachIndexed
		}


		val checkEscapeSymbolIndex = if (index - 1 >= 0) index - 1 else 0
		if (get(checkEscapeSymbolIndex) == '\\') return@forEachIndexed


		if (c == '\$' && get(index + 1) == '{') {
			var lastIndexToSkip = -1

			run findPairBracket@{
				forEachIndexed { index2, c2 ->
					if (index2 > index + 1) {


						if (c2 == '{') {
							skipBracketCount++
						} else if (c2 == '}') {
							if (skipBracketCount > 0) {
								skipBracketCount--
							} else {
								lastIndexToSkip = index2
								return@findPairBracket
							}
						}
					}
				}
			}

			skipSymbolsCount = lastIndexToSkip - index
		}
		if (c == input) {
			listForIndexes.add(index)
		}
	}
	return listForIndexes
}