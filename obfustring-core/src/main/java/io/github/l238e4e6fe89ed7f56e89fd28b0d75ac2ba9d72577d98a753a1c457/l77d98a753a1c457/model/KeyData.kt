package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.model

data class KeyData(
    var index: Int,
    val key: String,
) {
    fun increase() {
        index++
        if (index > key.length - 1) index = 0
    }

    fun getCode(): Int {
        return key[index].code
    }
}
