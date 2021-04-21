package com.copperleaf.trellis.impl.comparison

import com.copperleaf.trellis.base.BinaryOperationSpek
import com.copperleaf.trellis.base.Spek
import kotlin.jvm.JvmName

class EqualsSpek<Candidate, Operand>(
    lhs: Spek<Candidate, Operand>,
    rhs: Spek<Candidate, Operand>
) : BinaryOperationSpek<Candidate, Operand, Boolean>(
    lhs,
    rhs,
    cb = { a, b -> a() == b() }
)

infix fun <Candidate> Spek<Candidate, Boolean>.eq(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = EqualsSpek(
    this,
    other
)

@JvmName("unsafeEq")
@Suppress("UNCHECKED_CAST")
infix fun <Candidate> Spek<Candidate, *>.eq(
    other: Spek<Candidate, *>
): Spek<Candidate, Boolean> = EqualsSpek(
    this as Spek<Candidate, Boolean>,
    other as Spek<Candidate, Boolean>
)
