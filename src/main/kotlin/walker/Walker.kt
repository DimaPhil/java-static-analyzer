package walker

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import handler.Handler
import java.io.File
import java.io.Writer

class Walker(private val writer: Writer, private val handlers: List<Handler>) {
    fun walk(sourceDirectory: String) {
        File(sourceDirectory)
                .walk()
                .filter { !it.isDirectory && it.name.endsWith(".java") }
                .forEach { analyzeFile(it) }
    }

    private fun parseJavaFile(file: File): CompilationUnit? {
        return try {
            val parseResult = JavaParser().parse(file)
            if (!parseResult.isSuccessful) {
                throw AssertionError("can't parse ${file.name} - ${parseResult.problems}")
            }
            parseResult.result.get()
        } catch (exc: Throwable) {
            System.out.println("An error occurred during parsing the code: $exc")
            null
        }

    }

    private fun analyzeFile(file: File) {
        writer.write("Analyzing $file...\n")
        val compilationUnit = parseJavaFile(file) ?: throw AssertionError("File parsing failed, skipping")
        var errorFound = false
        handlers.forEach { handler ->
            compilationUnit.types.forEach(fun(classDeclaration) {
                // Skipping non-class and non-interface declarations
                if (classDeclaration !is ClassOrInterfaceDeclaration) {
                    return
                }
                val analyzeResult = StringBuilder()
                classDeclaration.fields.forEach { fieldDeclaration ->
                    handler.refresh()
                    val resultOfFieldDeclaration = handler.analyze(fieldDeclaration)
                    if (resultOfFieldDeclaration.isNotEmpty()) {
                        analyzeResult
                                .append("Fields check - ")
                                .append(handler.errorName)
                                .append(":\n")
                                .append(resultOfFieldDeclaration)
                                .append("\n")
                    }
                }
                classDeclaration.methods.forEach { methodDeclaration ->
                    handler.refresh()
                    val resultOfMethod = handler.analyze(methodDeclaration)
                    if (resultOfMethod.isNotEmpty()) {
                        analyzeResult
                                .append("Method \"${methodDeclaration.nameAsString}\" - ")
                                .append(handler.errorName)
                                .append(":\n")
                                .append(resultOfMethod)
                                .append("\n")
                    }
                }
                if (analyzeResult.isNotEmpty()) {
                    writer.write("""Code static analyzation failed:
                                    |Class ${getFullyQualifiedName(compilationUnit, classDeclaration)}:
                                    |$analyzeResult
                                    |""".trimMargin())
                    errorFound = true
                }
            })
        }
        if (!errorFound) {
            writer.write("OK, no issues found.\n\n")
        }
    }

    private fun getFullyQualifiedName(compilationUnit: CompilationUnit, classDeclaration: TypeDeclaration<*>): String {
        return compilationUnit.packageDeclaration
                .map{ it.name }
                .map{ it.toString() }
                .map { "$it." }
                .orElse("") + classDeclaration.nameAsString
    }
}
