package com.copperleaf.trellis.visitor

import com.copperleaf.trellis.base.Spek

/**
 * A simple Visitor which simply prints the name of the Spek being visited and its result.
 */
public class PrintlnVisitor : SpekVisitor {

    private var finished = false
    private var depth: Int = 0

    override fun enter(candidate: Spek<*, *>) {
        check(!finished) { "This visitor cannot be reused" }

        println("${indent}entering $candidate")
        depth++
    }

    override fun <U> leave(candidate: Spek<*, *>, result: U) {
        depth--
        println("${indent}leaving $candidate returned $result")
        if (depth == 0) {
            finished = true
        }
    }

    private val indent: String get() {
        return (0 until depth).map { "| " }.joinToString(separator = "")
    }
}
