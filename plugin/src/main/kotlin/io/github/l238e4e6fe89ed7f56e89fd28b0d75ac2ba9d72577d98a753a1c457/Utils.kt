package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457

import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.ObfStr
import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.ObfustringThis

internal object Templates {
    const val alreadyEncodedLog = "//@ | "
    val alreadyEncodedString = "${ObfStr::class.java.simpleName}(\""
    val obfustringThisAnnotation = "@${ObfustringThis::class.java.simpleName}"
    val import = "import ${ObfStr::class.java.`package`?.name}.${ObfStr::class.java.simpleName}"

    val encodedValue: (
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
    val encodedLog: (sb: StringBuilder) -> String = { sb ->
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