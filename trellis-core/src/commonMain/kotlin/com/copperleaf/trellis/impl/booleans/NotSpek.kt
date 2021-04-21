package com.copperleaf.trellis.impl.booleans

import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.base.UnaryOperationSpek
import kotlin.jvm.JvmName

@Suppress("UNCHECKED_CAST")
class NotSpek<Candidate>(
    lhs: Spek<Candidate, Boolean>
) : UnaryOperationSpek<Candidate, Boolean, Boolean>(
    lhs,
    cb = { a -> !a() }
)

fun <Candidate> Spek<Candidate, Boolean>.not(): Spek<Candidate, Boolean> = NotSpek(this)

@JvmName("unsafeNot")
fun <Candidate> Spek<Candidate, *>.not(): Spek<Candidate, Boolean> = NotSpek(this as Spek<Candidate, Boolean>)
