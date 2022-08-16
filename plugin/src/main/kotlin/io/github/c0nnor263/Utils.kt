package io.github.c0nnor263

import io.github.c0nnor263.obfustring_core.ObfStr
import io.github.c0nnor263.obfustring_core.ObfustringThis

internal class Templates {
    val alreadyEncodedLogTemp = "//@ | "
    val alreadyEncodedStringTemp = "${ObfStr::class.java.simpleName}(\""
    val annotationTemp = "@${ObfustringThis::class.java.simpleName}"
    val importTemp = "import ${ObfStr::class.java.`package`.name}.${ObfStr::class.java.simpleName}"


    val encodedValueTemp: (
        packageKey: String,
        encoder: ObfStr,
        encodedString: String
    ) -> String = { packageKey: String, encoder: ObfStr, stringToEncode: String ->

        "${ObfStr::class.simpleName}(\"$packageKey\")." +
                "${ObfStr::v.name}(" +
                encoder.v(stringToEncode, true) +
                ")"
    }
    val encodedLogTemp: (sb: StringBuilder) -> String = { sb ->
        "//@ | ${sb.trimStart()}"
    }
}