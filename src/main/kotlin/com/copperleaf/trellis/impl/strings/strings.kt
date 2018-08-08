package com.copperleaf.trellis.impl.strings

import com.copperleaf.trellis.api.Spek

class MinLengthSpek(val minLength: Int) : Spek<String, Boolean> {
    override suspend fun evaluate(candidate: String): Boolean {
        return candidate.length >= minLength
    }
}

class MaxLengthSpek(val minLength: Int) : Spek<String, Boolean> {
    override suspend fun evaluate(candidate: String): Boolean {
        return candidate.length <= minLength
    }
}
