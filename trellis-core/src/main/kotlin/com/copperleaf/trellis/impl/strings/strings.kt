package com.copperleaf.trellis.impl.strings

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.api.SpekVisitor
import com.copperleaf.trellis.api.ValueSpek
import com.copperleaf.trellis.api.visiting

class MinLengthSpek(private val minLength: Spek<String, Int>) : Spek<String, Boolean> {
    constructor(intVal: Int) : this(ValueSpek(intVal))

    override val children = listOf(minLength)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: String): Boolean {
        return visiting(visitor) { candidate.length >= minLength.evaluate(visitor, candidate) }
    }
}

class MaxLengthSpek(private val maxLength: Spek<String, Int>) : Spek<String, Boolean> {
    constructor(intVal: Int) : this(ValueSpek(intVal))

    override val children = listOf(maxLength)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: String): Boolean {
        return visiting(visitor) { candidate.length <= maxLength.evaluate(visitor, candidate) }
    }
}
