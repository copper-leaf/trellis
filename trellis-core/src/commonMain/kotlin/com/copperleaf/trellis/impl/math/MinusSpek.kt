package com.copperleaf.trellis.impl.math

import com.copperleaf.trellis.base.BinaryOperationSpek
import com.copperleaf.trellis.base.Spek
import kotlin.jvm.JvmName

public class MinusSpek<Candidate, Operand : Number>(
    lhs: Spek<Candidate, Operand>,
    rhs: Spek<Candidate, Operand>
) : BinaryOperationSpek<Candidate, Operand, Double>(
    lhs,
    rhs,
    cb = { a, b -> a().toDouble() - b().toDouble() }
)

public infix operator fun <Candidate> Spek<Candidate, Double>.minus(
    other: Spek<Candidate, Double>
): Spek<Candidate, Double> = MinusSpek(this, other)

@JvmName("unsafeMinus")
@Suppress("UNCHECKED_CAST")
public infix operator fun <Candidate> Spek<Candidate, *>.minus(
    other: Spek<Candidate, *>
): Spek<Candidate, Double> = MinusSpek(this as Spek<Candidate, Double>, other as Spek<Candidate, Double>)
