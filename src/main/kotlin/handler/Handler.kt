package handler

import com.github.javaparser.Position
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.nodeTypes.NodeWithRange

interface Handler {
    val errorName: String

    fun analyze(methodDeclaration: MethodDeclaration): String
    fun analyze(fieldDeclaration: FieldDeclaration): String

    fun refresh()

    private fun positionToString(position: Position): String {
        return if (position.line == -1 || position.column == -1)
            "Unknown position"
        else
            "(${position.line}, ${position.column})"
    }

    fun printPosition(result: StringBuilder, node: NodeWithRange<Node>) {
        result.append("Starts at: ")
                .append(positionToString(node.begin.orElse(Position(-1, -1))))
                .append(", ends at:")
                .append(positionToString(node.end.orElse(Position(-1, -1))))
                .append("\n")
                .append("Value: \"$node\"")
                .append("\n")
    }
}
