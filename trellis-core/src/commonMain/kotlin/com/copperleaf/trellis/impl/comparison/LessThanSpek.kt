package com.copperleaf.trellis.impl.comparison

import com.copperleaf.trellis.base.BinaryOperationSpek
import com.copperleaf.trellis.base.Spek
import kotlin.jvm.JvmName

public class LessThanSpek<Candidate, Operand : Comparable<Operand>>(
    lhs: Spek<Candidate, Operand>,
    rhs: Spek<Candidate, Operand>,
    private val allowEquals: Boolean
) : BinaryOperationSpek<Candidate, Operand, Boolean>(
    lhs,
    rhs,
    cb = { a, b ->
        if (allowEquals) {
            a() <= b()
        } else {
            a() < b()
        }
    }
)

public infix fun <Candidate> Spek<Candidate, Boolean>.lt(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = LessThanSpek(
    this,
    other,
    false
)

@JvmName("unsafeLt")
@Suppress("UNCHECKED_CAST")
public infix fun <Candidate> Spek<Candidate, *>.lt(
    other: Spek<Candidate, *>
): Spek<Candidate, Boolean> = LessThanSpek(
    this as Spek<Candidate, Boolean>,
    other as Spek<Candidate, Boolean>,
    false
)

public infix fun <Candidate> Spek<Candidate, Boolean>.lte(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = LessThanSpek(
    this,
    other,
    true
)

@JvmName("unsafeLte")
@Suppress("UNCHECKED_CAST")
public infix fun <Candidate> Spek<Candidate, *>.lte(
    other: Spek<Candidate, *>
): Spek<Candidate, Boolean> = LessThanSpek(
    this as Spek<Candidate, Boolean>,
    other as Spek<Candidate, Boolean>,
    true
)
