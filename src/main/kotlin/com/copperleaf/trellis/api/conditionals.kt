package com.copperleaf.trellis.api

// Extension Methods
//----------------------------------------------------------------------------------------------------------------------

fun <T, U> Spek<T, Boolean>.then(_then: Spek<T, U>, _else: Spek<T, U>): Spek<T, U> = IfSpek(this, _then, _else)

// Implementation Classes
//----------------------------------------------------------------------------------------------------------------------

class IfSpek<T, U>(
        private val _if: Spek<T, Boolean>,
        private val _then: Spek<T, U>,
        private val _else: Spek<T, U>
) : Spek<T, U> {

    override suspend fun evaluate(candidate: T): U {
        return if(_if.evaluate(candidate)) {
            _then.evaluate(candidate)
        }
        else {
            _else.evaluate(candidate)
        }
    }
}