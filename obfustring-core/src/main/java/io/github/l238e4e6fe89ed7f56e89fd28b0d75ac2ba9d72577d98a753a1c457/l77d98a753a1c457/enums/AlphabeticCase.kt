package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.enums

enum class AlphabeticCase(val startCode:Int) {
    UPPER_CASE(65),
    LOWER_CASE(97)
}

fun Char.alphabeticCase(): AlphabeticCase? {
    return when(this) {
        in 'A'..'Z' -> AlphabeticCase.UPPER_CASE
        in 'a'..'z' -> AlphabeticCase.LOWER_CASE
        else -> null
    }
}
