package handler

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.EmptyStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.stmt.TryStmt

class EmptyExceptionHandler : Handler {

    override val errorName: String
        get() = "Handler for empty catch clauses in try/catch constructions"

    override fun analyze(methodDeclaration: MethodDeclaration): String {
        val statement = methodDeclaration.body.orElseGet { BlockStmt() }
        return analyze(statement)
    }

    private fun analyze(statement: Statement): String {
        val result = StringBuilder()
        if (statement is TryStmt) {
            statement.catchClauses.forEach(fun(catchClause) {
                if (catchClause.parameter.nameAsString == IGNORED_EXCEPTION_NAME) {
                    return
                }
                val childNodes = catchClause.childNodes
                var isEmpty = childNodes.isEmpty()
                if (!isEmpty) {
                    isEmpty = true
                    childNodes
                            .filterNot { it is Parameter }
                            .filter { it is BlockStmt }
                            .forEach { isEmpty = isEmpty and isBlockStatementEmpty(it as BlockStmt) }
                }
                if (isEmpty) {
                    printPosition(result, catchClause)
                }
            })
        }
        statement.childNodes.forEach { if (it is Statement) result.append(analyze(it)) }
        return result.toString()
    }

    override fun analyze(fieldDeclaration: FieldDeclaration): String {
        return ""
    }

    override fun refresh() {}

    private fun isBlockStatementEmpty(blockStmt: BlockStmt): Boolean {
        val childNodes = blockStmt.childNodes
        if (childNodes.isEmpty()) {
            return true
        }
        return childNodes.all { it is EmptyStmt }
    }

    companion object {
        private const val IGNORED_EXCEPTION_NAME = "ignored"
    }
}
