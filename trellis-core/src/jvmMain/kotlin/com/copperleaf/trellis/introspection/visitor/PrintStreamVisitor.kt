package com.copperleaf.trellis.introspection.visitor

import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.visitor.SpekVisitor
import java.io.PrintStream

/**
 * A simple Visitor which simply prints the name of the Spek being visited and its result.
 */
class PrintStreamVisitor(val out: PrintStream = System.out) : SpekVisitor {

    private var finished = false
    private var depth: Int = 0

    override fun enter(candidate: Spek<*, *>) {
        check(!finished) { "This visitor cannot be reused" }

        out.println("${indent}entering $candidate")
        depth++
    }

    override fun <U> leave(candidate: Spek<*, *>, result: U) {
        depth--
        out.println("${indent}leaving $candidate returned $result")
        if (depth == 0) {
            finished = true
        }
    }

    private val indent: String get() {
        return (0 until depth).map { "| " }.joinToString(separator = "")
    }
}
