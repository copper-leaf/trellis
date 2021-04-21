package com.copperleaf.trellis.impl.math

import com.copperleaf.trellis.base.BinaryOperationSpek
import com.copperleaf.trellis.base.Spek
import kotlin.jvm.JvmName

class DivideSpek<Candidate, Operand : Number>(
    lhs: Spek<Candidate, Operand>,
    rhs: Spek<Candidate, Operand>
) : BinaryOperationSpek<Candidate, Operand, Double>(
    lhs,
    rhs,
    cb = { a, b -> a().toDouble() / b().toDouble() }
)

infix operator fun <Candidate> Spek<Candidate, Double>.div(
    other: Spek<Candidate, Double>
): Spek<Candidate, Double> = DivideSpek(this, other)

@JvmName("unsafeDivide")
@Suppress("UNCHECKED_CAST")
infix operator fun <Candidate> Spek<Candidate, *>.div(
    other: Spek<Candidate, *>
): Spek<Candidate, Double> = DivideSpek(this as Spek<Candidate, Double>, other as Spek<Candidate, Double>)
