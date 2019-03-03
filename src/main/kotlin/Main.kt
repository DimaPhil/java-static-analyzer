import handler.*
import walker.Walker
import java.io.File

import java.io.Writer

private fun createWriter(outputFilename: String?): Writer {
    val filename = outputFilename ?: "output.txt"
    return File(filename).printWriter()
}

fun main(args: Array<String>) {
    if (args.isEmpty() || args.size > 2) {
        println("Usage: java Main <sources root directory> [<output file>]")
        return
    }
    val sourceDirectory = args[0]
    val outputFilename = args.getOrNull(1)
    val handlers = listOf(
            EmptyExceptionHandler(),
            UnusedVariableHandler(),
            UninitializedVariableHandler(),
            ConstantVariableNamingHandler(),
            UnusedMethodArgument()
    )
    createWriter(outputFilename).use { Walker(it, handlers).walk(sourceDirectory) }
}
