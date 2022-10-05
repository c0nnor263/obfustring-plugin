package io.github.a26197993b77e31a4.core


@JvmInline
value class ObfStr(private val key: String) {
    fun v(string: String, encrypt: Boolean = false): String = with(string) {
        if (key.isBlank()) throw IllegalArgumentException("Key must not be empty for obfustring plugin")
        val stringBuilder = StringBuilder()
        var keyLoopIndex = 0
        var skipSymbolsCount = 0
        var isEscape = false
        var encryptSymbolsPair = 0

        string.forEachIndexed { currentIndex, currentChar ->
            if (skipSymbolsCount != 0) {
                skipSymbolsCount--
                stringBuilder.append(currentChar)
                if (skipSymbolsCount == 0 && encrypt) {
                    if (!isEscape) {
                        stringBuilder.append("¦")
                    } else {
                        isEscape = false
                    }
                }
                return@forEachIndexed
            }

            val symbolCodeToAdd = when (currentChar) {
                in 'A'..'Z' -> 65
                in 'a'..'z' -> 97
                '\$' -> {
                    var countOfSkipBracket = 0
                    var lastIndexToSkip: Int = -1
                    var isQuoteBracket = false

                    run index@{

                        string.forEachIndexed loop@{ index, char ->
                            if (index >= currentIndex) {
                                fun addSplitSymbol(quoteBracket: Boolean = false) {
                                    isQuoteBracket = quoteBracket

                                    lastIndexToSkip = index
                                    return
                                }

                                when {

                                    string[currentIndex + 1] == '\$' -> {
                                        return@index addSplitSymbol()
                                    }

                                    // Skips inner templates
                                    string[currentIndex + 1] == '{' -> {
                                        if (char == '\$' && string[index + 1] == '{') {
                                            countOfSkipBracket++
                                        } else if (char == '}') {
                                            countOfSkipBracket--
                                            if (countOfSkipBracket == 0) {
                                                return@index addSplitSymbol()
                                            }
                                        }
                                    }
                                    char == '\$' && string[index + 1] == '{' -> {
                                        return@index addSplitSymbol(true)
                                    }

                                    char == '"' || char == ' ' -> {
                                        return@index addSplitSymbol(true)
                                    }
                                    char == '\\' -> {
                                        skipSymbolsCount = 1
                                        return@index addSplitSymbol(true)
                                    }

                                }

                            }
                        }
                    }

                    if (lastIndexToSkip == -1) {
                        lastIndexToSkip = string.length - 1
                    }
                    skipSymbolsCount = lastIndexToSkip - currentIndex - if (isQuoteBracket) 1 else 0

                    stringBuilder.append('¦')
                    stringBuilder.append(currentChar)
                    return@forEachIndexed
                }
                '¦' -> {
                    var lastIndexToSkip = -1
                    encryptSymbolsPair++
                    if (encryptSymbolsPair == 2) {
                        encryptSymbolsPair = 0
                        return@forEachIndexed
                    }
                    run loop@{
                        string.forEachIndexed { index, char ->
                            if (index > currentIndex && char == '¦') {
                                lastIndexToSkip = index
                                return@loop
                            }
                        }
                    }

                    skipSymbolsCount = lastIndexToSkip - currentIndex - 1
                    return@forEachIndexed
                }
                '\\' -> {
                    skipSymbolsCount = 1
                    isEscape = true
                    stringBuilder.append(currentChar)
                    return@forEachIndexed
                }
                else -> {
                    stringBuilder.append(currentChar)
                    return@forEachIndexed
                }
            }

            val encryptInt = 90
            val decryptInt = 26 + when (symbolCodeToAdd) {
                65 -> 38
                else -> 0
            }

            val value = if (encrypt) {
                (currentChar.code + key[keyLoopIndex].code - encryptInt) % 26
            } else {
                (currentChar.code - key[keyLoopIndex].code + decryptInt) % 26
            }.plus(symbolCodeToAdd).toChar()

            keyLoopIndex++
            if (keyLoopIndex > key.length - 1) keyLoopIndex = 0
            stringBuilder.append(value)
        }
        return stringBuilder.toString()
    }
}