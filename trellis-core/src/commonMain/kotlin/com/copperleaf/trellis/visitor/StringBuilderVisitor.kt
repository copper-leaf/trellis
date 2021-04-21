package com.copperleaf.trellis.visitor

import com.copperleaf.trellis.base.Spek

/**
 * A simple Visitor which simply prints the name of the Spek being visited and its result.
 */
class StringBuilderVisitor(val out: StringBuilder = StringBuilder()) : SpekVisitor {

    private var finished = false
    private var depth: Int = 0

    override fun enter(candidate: Spek<*, *>) {
        check(!finished) { "This visitor cannot be reused" }

        out.appendLine("${indent}entering $candidate")
        depth++
    }

    override fun <Result> leave(candidate: Spek<*, *>, result: Result) {
        depth--
        out.appendLine("${indent}leaving $candidate returned $result")
        if (depth == 0) {
            finished = true
        }
    }

    private val indent: String get() {
        return (0 until depth).map { "| " }.joinToString(separator = "")
    }
}
