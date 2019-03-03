package handler

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.Statement

class UninitializedVariableHandler : Handler {
    private var usedInSet: MutableSet<String> = HashSet()

    override val errorName: String
        get() = "Handler for unused variables"

    override fun analyze(methodDeclaration: MethodDeclaration): String {
        val statement = methodDeclaration.body.orElseGet { BlockStmt() }
        return analyze(statement)
    }

    private fun analyze(statement: Statement): String {
        fun helper(result: StringBuilder, statement: Statement) {
            statement.childNodes.forEach { if (it is Statement) helper(result, it) }
            when (statement) {
                is ExpressionStmt -> {
                    val expression = statement.expression
                    when (expression) {
                        is VariableDeclarationExpr -> {
                            expression.variables.forEach { variable ->
                                if (variable.initializer.isPresent) {
                                    usedInSet.add(variable.name.asString())
                                }
                            }
                        }
                        is AssignExpr -> {
                            if (expression.target.isNameExpr) {
                                usedInSet.add(expression.target.asNameExpr().nameAsString)
                            }
                            helper(result, ExpressionStmt(expression.value))
                        }
                        is NameExpr -> {
                            val name = expression.name.asString()
                            if (!usedInSet.contains(name)) {
                                result.append("Variable $name is not assigned, but used\n")
                            }
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

    override fun analyze(fieldDeclaration: FieldDeclaration): String {
        return ""
    }

    override fun refresh() {
        usedInSet = HashSet()
    }
}
