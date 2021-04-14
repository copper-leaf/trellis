package com.copperleaf.trellis.impl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting

class MinLengthSpek(private val minLength: Spek<String, Int>) : Spek<String, Boolean> {
    constructor(intVal: Int) : this(ValueSpek(intVal))

    override val children = listOf(minLength)

    override fun evaluate(visitor: SpekVisitor, candidate: String): Boolean {
        return visiting(visitor) { candidate.length >= minLength.evaluate(visitor, candidate) }
    }
}

class MaxLengthSpek(private val maxLength: Spek<String, Int>) : Spek<String, Boolean> {
    constructor(intVal: Int) : this(ValueSpek(intVal))

    override val children = listOf(maxLength)

    override fun evaluate(visitor: SpekVisitor, candidate: String): Boolean {
        return visiting(visitor) { candidate.length <= maxLength.evaluate(visitor, candidate) }
    }
}

class StringLengthSpek : Spek<String, Int> {
    override fun evaluate(visitor: SpekVisitor, candidate: String): Int {
        return visiting(visitor) { candidate.length }
    }
}
