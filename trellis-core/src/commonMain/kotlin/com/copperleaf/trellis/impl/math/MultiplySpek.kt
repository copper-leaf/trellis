package com.copperleaf.trellis.impl.math

import com.copperleaf.trellis.base.BinaryOperationSpek
import com.copperleaf.trellis.base.Spek
import kotlin.jvm.JvmName

class MultiplySpek<Candidate, Operand : Number>(
    lhs: Spek<Candidate, Operand>,
    rhs: Spek<Candidate, Operand>
) : BinaryOperationSpek<Candidate, Operand, Double>(
    lhs,
    rhs,
    cb = { a, b -> a().toDouble() * b().toDouble() }
)

infix operator fun <Candidate> Spek<Candidate, Double>.times(
    other: Spek<Candidate, Double>
): Spek<Candidate, Double> = MultiplySpek(this, other)

@JvmName("unsafeMultiply")
@Suppress("UNCHECKED_CAST")
infix operator fun <Candidate> Spek<Candidate, *>.times(
    other: Spek<Candidate, *>
): Spek<Candidate, Double> = MultiplySpek(this as Spek<Candidate, Double>, other as Spek<Candidate, Double>)
