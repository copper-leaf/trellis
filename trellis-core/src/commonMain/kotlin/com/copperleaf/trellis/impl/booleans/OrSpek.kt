package com.copperleaf.trellis.impl.booleans

import com.copperleaf.trellis.base.BinaryOperationSpek
import com.copperleaf.trellis.base.Spek
import kotlin.jvm.JvmName

class OrSpek<Candidate>(
    lhs: Spek<Candidate, Boolean>,
    rhs: Spek<Candidate, Boolean>,
) : BinaryOperationSpek<Candidate, Boolean, Boolean>(
    lhs,
    rhs,
    cb = { a, b -> a() || b() }
)

infix fun <Candidate> Spek<Candidate, Boolean>.or(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = OrSpek(this, other)

@JvmName("unsafeOr")
@Suppress("UNCHECKED_CAST")
infix fun <Candidate> Spek<Candidate, *>.or(
    other: Spek<Candidate, *>
): Spek<Candidate, Boolean> = OrSpek(this as Spek<Candidate, Boolean>, other as Spek<Candidate, Boolean>)

infix fun <Candidate> Spek<Candidate, Boolean>.orNot(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = OrSpek(this, other.not())
