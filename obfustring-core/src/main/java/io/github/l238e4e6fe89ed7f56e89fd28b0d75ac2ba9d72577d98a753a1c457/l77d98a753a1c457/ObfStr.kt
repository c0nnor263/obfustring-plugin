package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457

import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.enums.SymbolType
import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.enums.alphabeticCase
import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.model.Counter
import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.model.Encrypt.Companion.formatRawValue
import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.model.KeyData
import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.model.PassHolder

/**
 * Obfuscates your strings in annotated class
 *
 * [v] - Vigenere cipher method
 */
@JvmInline
value class ObfStr(val key: String) {
    fun v(string: String, encrypt: Boolean = false): String = with(string) {
        if (key.isBlank()) throw IllegalArgumentException("Key must not be empty for obfustring plugin")
        val stringBuilder = StringBuilder()
        val counter = Counter(
            encrypt = encrypt,
            keyData = KeyData(0, key)
        )
        val passHolder = PassHolder()

        string.forEachIndexed mainIterator@{ currentIndex, currentChar ->
            passHolder.reset()
            if (stringBuilder.appendObfustring(
                    currentChar,
                    counter,
                    passHolder
                )
            ) return@mainIterator


            when (currentChar) {
                SymbolType.SKIP_SYMBOLS_PAIR.symbol -> {

                    counter.skipPairCount++
                    if (counter.skipPairCount == 2) {
                        counter.skipPairCount = 0
                    } else {
                        var index = 0
                        for (char in string) {
                            if (index > currentIndex && char == '¦') {
                                passHolder.indexToSkip = index - 1
                                break
                            }
                            index++

                        }
                        passHolder.passBy(currentIndex)
                    }
                    return@mainIterator
                }

                SymbolType.PARAMETRIZED.symbol -> {
                    var index = 0

                    for (char in string) {
                        if (index >= currentIndex) {
                            when {
                                // Skips inner templates
                                string[currentIndex + 1] == '{' -> {
                                    if (char == '\$' && string[index + 1] == '{') {
                                        passHolder.quoteData.leftCount++
                                        continue
                                    } else if (char == '}') {
                                        passHolder.quoteData.leftCount--
                                        if (passHolder.quoteData.leftCount == 0) {
                                            passHolder.addSplitSymbol(index)
                                            break
                                        }
                                    }
                                }


                                string[currentIndex + 1] == '\$' -> {
                                    passHolder.addSplitSymbol(
                                        index
                                    )
                                    break
                                }

                                (char == '\$' && string[index + 1] == '{')
                                        || (char == '"' || char == ' ') -> {
                                    passHolder.addSplitSymbol(
                                        index,
                                        true
                                    )
                                    break
                                }

                                char == '\\' -> {
                                    passHolder.leftCount = 1
                                    passHolder.addSplitSymbol(
                                        index,
                                        true
                                    )
                                    break
                                }
                            }

                        }
                        index++
                    }


                    passHolder.passBy(currentIndex)
                    stringBuilder.append('¦')
                    stringBuilder.append(currentChar)
                }

                SymbolType.SLASH.symbol -> {
                    stringBuilder.append(currentChar)
                    passHolder.passOneTime()
                }

                else -> stringBuilder.append(currentChar)
            }
        }

        return stringBuilder.toString()
    }
}

fun StringBuilder.appendObfustring(
    currentChar: Char,
    counter: Counter,
    passHolder: PassHolder
): Boolean {
    return if (counter.makeOneSkip(this, currentChar, passHolder) ?: return true) {
        val case = currentChar.alphabeticCase() ?: return false
        val formatValue = currentChar.code.formatRawValue(
            case,
            counter
        )
        append(formatValue)
        true
    } else false
}
