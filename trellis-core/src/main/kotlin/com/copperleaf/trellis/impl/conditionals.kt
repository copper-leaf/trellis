package com.copperleaf.trellis.impl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting

// Extension Methods
//----------------------------------------------------------------------------------------------------------------------

fun <T, U> Spek<T, Boolean>.then(_then: Spek<T, U>, _else: Spek<T, U>): Spek<T, U> =
    IfSpek(this, _then, _else)

// Implementation Classes
//----------------------------------------------------------------------------------------------------------------------

class IfSpek<T, U>(
    private val _if: Spek<T, Boolean>,
    private val _then: Spek<T, U>,
    private val _else: Spek<T, U>
) : Spek<T, U> {

    override val children = listOf(_if, _then, _else)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): U {
        return visiting(visitor) {
            if (_if.evaluate(visitor, candidate)) {
                _then.evaluate(visitor, candidate)
            } else {
                _else.evaluate(visitor, candidate)
            }
        }
    }
}