package com.copperleaf.trellis.util

import com.copperleaf.trellis.base.Spek

public fun Spek<*, *>.printSpekTree(): String {
    return printSpekTree(0, emptyList())
}

private fun Spek<*, *>.printSpekTree(
    currentIndent: Int,
    visitedSpeks: List<Spek<*, *>>
): String {
    return when (this.children.size) {
        0 -> printTerminalSpekTree(currentIndent)
        else -> printNonTerminalSpekTree(currentIndent, visitedSpeks)
    }
}

private fun Spek<*, *>.printTerminalSpekTree(
    currentIndent: Int
): String {
    return "${indent(currentIndent)}($spekName)"
}

private fun Spek<*, *>.printNonTerminalSpekTree(
    currentIndent: Int,
    visitedSpeks: List<Spek<*, *>>
): String {
    val childrenPrinted = if (this in visitedSpeks) {
        "${indent(currentIndent + 2)}...recursive call"
    } else {
        children
            .map { it.printSpekTree(currentIndent + 2, visitedSpeks + this) }
            .joinToString(separator = "\n")
    }

    return "${indent(currentIndent)}($spekName:\n" +
        childrenPrinted +
        "\n" +
        "${indent(currentIndent)})"
}

private fun indent(currentIndent: Int): String {
    return (0 until currentIndent).map { " " }.joinToString(separator = "")
}
