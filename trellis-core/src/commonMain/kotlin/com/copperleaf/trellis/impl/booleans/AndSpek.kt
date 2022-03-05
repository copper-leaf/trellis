package com.copperleaf.trellis.impl.booleans

import com.copperleaf.trellis.base.BinaryOperationSpek
import com.copperleaf.trellis.base.Spek
import kotlin.jvm.JvmName

public class AndSpek<Candidate>(
    lhs: Spek<Candidate, Boolean>,
    rhs: Spek<Candidate, Boolean>
) : BinaryOperationSpek<Candidate, Boolean, Boolean>(
    lhs,
    rhs,
    cb = { a, b -> a() && b() }
)

public infix fun <Candidate> Spek<Candidate, Boolean>.and(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = AndSpek(this, other)

@JvmName("unsafeAnd")
@Suppress("UNCHECKED_CAST")
public infix fun <Candidate> Spek<Candidate, *>.and(
    other: Spek<Candidate, *>
): Spek<Candidate, Boolean> = AndSpek(this as Spek<Candidate, Boolean>, other as Spek<Candidate, Boolean>)

public infix fun <Candidate> Spek<Candidate, Boolean>.andNot(
    other: Spek<Candidate, Boolean>
): Spek<Candidate, Boolean> = AndSpek(this, other.not())
