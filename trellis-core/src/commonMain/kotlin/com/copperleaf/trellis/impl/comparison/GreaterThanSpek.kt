package com.copperleaf.trellis.impl.comparison

import com.copperleaf.trellis.base.BinaryOperationSpek
import com.copperleaf.trellis.base.Spek
import kotlin.jvm.JvmName

class GreaterThanSpek<Candidate, Operand : Comparable<Operand>>(
    lhs: Spek<Candidate, Operand>,
    rhs: Spek<Candidate, Operand>,
    private val allowEquals: Boolean
) : BinaryOperationSpek<Candidate, Operand, Boolean>(
    lhs,
    rhs,
    cb = { a, b ->
        if (allowEquals) {
            a() >= b()
        } else {
            a() > b()
        }
    }
)

infix fun <Candidate> Spek<Candidate, Boolean>.gt(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = GreaterThanSpek(
    this,
    other,
    false
)

@JvmName("unsafeGt")
@Suppress("UNCHECKED_CAST")
infix fun <Candidate> Spek<Candidate, *>.gt(
    other: Spek<Candidate, *>
): Spek<Candidate, Boolean> = GreaterThanSpek(
    this as Spek<Candidate, Boolean>,
    other as Spek<Candidate, Boolean>,
    false
)

infix fun <Candidate> Spek<Candidate, Boolean>.gte(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = GreaterThanSpek(
    this,
    other,
    true
)

@JvmName("unsafeGte")
@Suppress("UNCHECKED_CAST")
infix fun <Candidate> Spek<Candidate, *>.gte(
    other: Spek<Candidate, *>
): Spek<Candidate, Boolean> = GreaterThanSpek(
    this as Spek<Candidate, Boolean>,
    other as Spek<Candidate, Boolean>,
    true
)
