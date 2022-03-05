package com.copperleaf.trellis.impl.conditionals

import com.copperleaf.trellis.base.BaseSpek
import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

public class IfSpek<Candidate, Result>(
    private val _if: Spek<Candidate, Boolean>,
    private val _then: Spek<Candidate, Result>,
    private val _else: Spek<Candidate, Result>
) : BaseSpek<Candidate, Result>(_if, _then, _else) {

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) {
            if (_if.evaluate(visitor, candidate)) {
                _then.evaluate(visitor, candidate)
            } else {
                _else.evaluate(visitor, candidate)
            }
        }
    }
}

public fun <Candidate, Result> Spek<Candidate, Boolean>.then(
    _then: Spek<Candidate, Result>,
    _else: Spek<Candidate, Result>
): Spek<Candidate, Result> =
    IfSpek(this, _then, _else)
