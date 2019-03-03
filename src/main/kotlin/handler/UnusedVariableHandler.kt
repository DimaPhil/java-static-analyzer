package handler

import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.Statement

class UnusedVariableHandler : Handler {
    private var variables: MutableSet<String> = HashSet()
    private var usedInSet: MutableSet<String> = HashSet()
    private var usedInGet: MutableSet<String> = HashSet()

    override val errorName: String
        get() = "Handler for unused variables"

    override fun analyze(methodDeclaration: MethodDeclaration): String {
        val statement = methodDeclaration.body.orElseGet { BlockStmt() }
        return analyze(statement)
    }

    private fun analyze(statement: Statement): String {
        fun helper(statement: Statement) {
            statement.childNodes.forEach { if (it is Statement) helper(it) }
            when (statement) {
                is ExpressionStmt -> {
                    val expression = statement.expression
                    when (expression) {
                        is VariableDeclarationExpr -> {
                            expression.variables.forEach { variable ->
                                if (variable.initializer.isPresent) {
                                    usedInSet.add(variable.name.asString())
                                }
                                variables.add(variable.name.asString())
                            }
                        }
                        is AssignExpr -> {
                            if (expression.target.isNameExpr) {
                                val name = expression.target.asNameExpr().nameAsString
                                if (variables.contains(name)) {
                                    usedInSet.add(name)
                                }
                            }
                            helper(ExpressionStmt(expression.value))
                        }
                        is NameExpr -> usedInGet.add(expression.name.asString())
                        is UnaryExpr -> {
                            helper(ExpressionStmt(expression.expression))
                        }
                        is BinaryExpr -> {
                            helper(ExpressionStmt(expression.left))
                            helper(ExpressionStmt(expression.right))
                        }
                    }
                }
            }
        }

        helper(statement)
        val result = StringBuilder()
        for (variable in variables) {
            if (variable !in usedInGet && variable !in usedInSet) {
                result.append("Variable $variable in never being set and get\n")
            } else if (variable !in usedInGet) {
                result.append("Variable $variable was set, but never used\n")
            } else if (variable !in usedInSet) {
                result.append("Variable $variable wasn't set, but is being used in expressions\n")
            }
        }
        return result.toString()
    }

    override fun analyze(fieldDeclaration: FieldDeclaration): String {
        return ""
    }

    override fun refresh() {
        variables = HashSet()
        usedInSet = HashSet()
        usedInGet = HashSet()
    }
}
