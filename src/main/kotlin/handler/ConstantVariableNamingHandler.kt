package handler

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import java.lang.StringBuilder

class ConstantVariableNamingHandler : Handler {
    override fun analyze(fieldDeclaration: FieldDeclaration): String {
        val result = StringBuilder()
        if (fieldDeclaration.isFinal && fieldDeclaration.isStatic) {
            fieldDeclaration.variables.forEach { variable ->
                val name = variable.nameAsString
                if (name != name.toUpperCase()) {
                    result.append("Variable $name is final and static, but its name is not in uppercase\n")
                }
            }
        }
        return result.toString()
    }

    override val errorName: String
        get() = "Handler for constant variable naming"

    override fun analyze(methodDeclaration: MethodDeclaration): String {
        return ""
    }

    override fun refresh() {
    }

}