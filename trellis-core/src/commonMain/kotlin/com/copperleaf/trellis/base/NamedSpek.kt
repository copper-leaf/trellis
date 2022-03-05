package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

/**
 * Wrap a spek and give it a name
 */
public class NamedSpek<Candidate, Result>(
    public val name: String,
    public val base: Spek<Candidate, Result>
) : BaseSpek<Candidate, Result>(base) {

    override val spekName: String = "[$name]"

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) { base.evaluate(visitor, candidate) }
    }
}

public fun <Candidate, Result> Spek<Candidate, Result>.named(name: String): Spek<Candidate, Result> {
    return NamedSpek(name, this)
}
