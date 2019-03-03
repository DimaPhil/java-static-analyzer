package handler

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.Statement

class UnusedMethodArgument : Handler {
    private var argumentNames: MutableSet<String> = HashSet();

    override val errorName: String
        get() = "Handler for unused method argument"

    override fun analyze(methodDeclaration: MethodDeclaration): String {
        val name = methodDeclaration.nameAsString
        if (name == "main") {
            return ""
        }

        methodDeclaration.parameters.map { it.nameAsString }.forEach { argumentNames.add(it) }
        methodDeclaration.body.ifPresent { analyze(it) }
        val result = StringBuilder()
        argumentNames.forEach { result.append("Variable $it is unused\n") }
        return result.toString()
    }

    private fun analyze(statement: Statement): String {
        fun helper(result: StringBuilder, statement: Statement) {
            statement.childNodes.forEach { if (it is Statement) helper(result, it) }
            when (statement) {
                is ExpressionStmt -> {
                    val expression = statement.expression
                    when (expression) {
                        is NameExpr -> {
                            val name = expression.name.asString()
                            argumentNames.remove(name)
                        }
                        is UnaryExpr -> {
                            helper(result, ExpressionStmt(expression.expression))
                        }
                        is BinaryExpr -> {
                            helper(result, ExpressionStmt(expression.left))
                            helper(result, ExpressionStmt(expression.right))
                        }
                    }
                }
            }
        }

        val result = StringBuilder()
        helper(result, statement)
        return result.toString()
    }

    override fun analyze(fieldDeclaration: FieldDeclaration): String = ""

    override fun refresh() {
        argumentNames = HashSet();
    }

}