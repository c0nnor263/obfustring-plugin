package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.model

import io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457.l77d98a753a1c457.enums.AlphabeticCase

class Encrypt {
    companion object {
        private const val ENCRYPT_INT = 90
        private const val DECRYPT_INT = 26
        private const val UPPER_CASE_DECRYPT = 38


        fun Int.formatRawValue(case: AlphabeticCase, counter: Counter): Char {

            val encryptInt = ENCRYPT_INT
            val decryptInt = DECRYPT_INT + when (case) {
                AlphabeticCase.UPPER_CASE -> UPPER_CASE_DECRYPT
                else -> 0
            }


            val keyCharCode = counter.keyData.getCode()
            val processedValue = if (counter.encrypt) {
                (this + keyCharCode - encryptInt) % DECRYPT_INT
            } else {
                (this - keyCharCode + decryptInt) % DECRYPT_INT
            }.plus(case.startCode).toChar()

            counter.keyData.increase()
            return processedValue
        }
    }


}