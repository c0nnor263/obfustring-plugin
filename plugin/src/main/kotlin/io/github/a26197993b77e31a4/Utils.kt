package io.github.a26197993b77e31a4

import io.github.a26197993b77e31a4.core.ObfustringThis
import io.github.a26197993b77e31a4.core.ObfStr

internal class Templates {
    val alreadyEncodedLogTemp = "//@ | "
    val alreadyEncodedStringTemp = "${ObfStr::class.java.simpleName}(\""
    val annotationTemp = "@${ObfustringThis::class.java.simpleName}"
    val importTemp =
        "import io.github.c0nnor263.obfustring.${ObfStr::class.java.simpleName}"


    val encodedValueTemp: (
        packageKey: String,
        encoder: ObfStr,
        encodedString: String
    ) -> String = { packageKey: String, encoder: ObfStr, stringToEncode: String ->

        "${ObfStr::class.simpleName}(\"$packageKey\")." +
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