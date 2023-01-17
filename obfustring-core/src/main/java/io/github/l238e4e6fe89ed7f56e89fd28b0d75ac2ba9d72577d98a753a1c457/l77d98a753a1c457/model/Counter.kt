package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.model

data class Counter(
    val encrypt: Boolean = false,
    var skipPairCount: Int = 0,
    val keyData: KeyData,
) {
    fun makeOneSkip(sb: StringBuilder, char: Char, passHolder: PassHolder): Boolean? {
        return if (passHolder.leftCount != 0) {
            passHolder.leftCount--
            sb.append(char)

            if (passHolder.leftCount == 0 && encrypt) {
                if (!passHolder.isPass) {
                    sb.append("¦")
                } else {
                    passHolder.isPass = false
                }
            }
            null
        } else true
    }
}
