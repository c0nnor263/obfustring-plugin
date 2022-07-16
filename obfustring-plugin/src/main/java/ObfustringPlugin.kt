import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File


class ObfustringPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        var isAlreadyStarted = false
        project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            .onVariants { variant ->
                val packageName = variant.applicationId
                if (!isAlreadyStarted) {
                    beginWork(project, packageName)
                    isAlreadyStarted = true
                }
            }
    }

    private fun beginWork(project: Project, packageName: Property<String>) {
        val encoder = ObfustringEncoder(packageName.get().filter { it != '.' })
        val classesTree =
            project.fileTree("src/main/java").filter { it.isFile && it.extension == "kt" }.files
        println(project.fileTree("src"))
        classesTree.forEach processClasses@{ file ->
            processFile(encoder, file)
        }
    }

    private fun processFile(encoder: ObfustringEncoder, file: File) {
        if (!file.readText().contains("@${Obfustring::class.java.name}")) return

        val pendingLogCheck: MutableList<(() -> Unit)?> = mutableListOf()

        val list = file.readLines().toMutableList()
        list.forEach { line ->
            if (line.contains(
                    "${ObfustringEncoder::class.simpleName}()" +
                            ".${ObfustringEncoder::vigenere.name}(\""
                ) ||
                line.indexOfFirst { it == '"' } < line.indexOf("const va") ||
                line.startsWith('@') ||
                line.startsWith("//")
            ) return@forEach

            if (line.contains("Log.")) {
                val startBracket = list.indexOf(line)
                val sb = StringBuilder(line)
                run searchBracket@{
                    list.forEachIndexed { index, checkLine ->
                        if (index >= startBracket) {
                            val closeBracket = checkLine.lastIndexOf(")")
                            if (closeBracket != -1) return@searchBracket
                            sb.append(checkLine)
                        }
                    }
                }
                val currentLineIndex = list.indexOf(line)
                pendingLogCheck.add {
                    val newLine = "//@ | ${sb.trimStart()}"
                    if (list.any { it != newLine }) {
                        if (list[currentLineIndex - 1].contains("//@")) {
                            list.removeAt(currentLineIndex - 1)
                        }
                        list.add(currentLineIndex, newLine)
                    }
                }
            }

            val index = list.indexOf(line)

            val lineMap = mutableMapOf<Int, Int>()

            var firstOccurIndex = -1
            var secondOccurIndex = -1
            var quotesOccurCount = 0

            line.forEachIndexed { indexC: Int, c: Char ->
                if (c == '"') {
                    when (quotesOccurCount) {
                        0 -> firstOccurIndex = indexC
                        1 -> secondOccurIndex = indexC
                    }
                    quotesOccurCount++
                    if (quotesOccurCount > 1) {
                        lineMap[firstOccurIndex] = secondOccurIndex
                        quotesOccurCount = 0
                        return@forEachIndexed
                    }
                }
            }

            for ((firstIndex, secondIndex) in lineMap) {
                if (firstIndex == -1 || secondIndex == -1) {
                    continue
                }
                val foundString = line.substring(firstIndex, secondIndex + 1)

                val newValue =
                    "${ObfustringEncoder::class.simpleName}()" +
                            ".${ObfustringEncoder::vigenere.name}(${
                                encoder.vigenere(foundString)
                            })"
                println("> Processing $file")

                val edit = line.replace(foundString, newValue)
                list[index] = edit
            }
        }
        pendingLogCheck.reversed().forEach {
            it?.invoke()
        }
        pendingLogCheck.clear()
        File(file.absolutePath).writeText(list.joinToString("\n"))
    }
}