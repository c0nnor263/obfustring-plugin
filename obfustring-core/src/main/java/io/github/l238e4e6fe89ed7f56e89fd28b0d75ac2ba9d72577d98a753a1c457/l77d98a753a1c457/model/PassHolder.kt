package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.model

data class PassHolder(
    var isPass: Boolean = false,
    var leftCount: Int = 0,
    var indexToSkip: Int = -1,
    val quoteData: QuoteData = QuoteData()
) {
    fun passOneTime() {
        leftCount = 1
        isPass = true
    }

    fun passBy(currentLoopIndex: Int) {
        leftCount = indexToSkip - currentLoopIndex - if (quoteData.isQuoteBracket) 1 else 0
        println("passBy $leftCount")
    }

    fun reset() {
        quoteData.isQuoteBracket = false
        indexToSkip = -1
    }

    fun addSplitSymbol(index: Int, quoteBracket: Boolean = false) {
        quoteData.isQuoteBracket = quoteBracket
        indexToSkip = index
    }
}
