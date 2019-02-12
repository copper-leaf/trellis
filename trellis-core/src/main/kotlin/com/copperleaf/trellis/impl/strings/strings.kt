package com.copperleaf.trellis.impl.strings

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.api.ValueSpek

class MinLengthSpek(private val minLength: Spek<String, Int>) : Spek<String, Boolean> {
    constructor(intVal: Int) : this(ValueSpek(intVal))
    override suspend fun evaluate(candidate: String): Boolean {
        return candidate.length >= minLength.evaluate(candidate)
    }
}

class MaxLengthSpek(private val maxLength: Spek<String, Int>) : Spek<String, Boolean> {
    constructor(intVal: Int) : this(ValueSpek(intVal))
    override suspend fun evaluate(candidate: String): Boolean {
        return candidate.length <= maxLength.evaluate(candidate)
    }
}
