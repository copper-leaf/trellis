package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

/**
 * Wrap a spek and give it a name
 */
class NamedSpek<Candidate, Result>(
    val name: String,
    val base: Spek<Candidate, Result>
) : BaseSpek<Candidate, Result>(base) {

    override val spekName: String = "[$name]"

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) { base.evaluate(visitor, candidate) }
    }
}

fun <Candidate, Result> Spek<Candidate, Result>.named(name: String): Spek<Candidate, Result> {
    return NamedSpek(name, this)
}
