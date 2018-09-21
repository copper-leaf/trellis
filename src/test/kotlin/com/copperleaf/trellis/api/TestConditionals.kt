package com.copperleaf.trellis.api

import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TestConditionals {

    @Test
    fun testIf() = runBlocking<Unit> {
        val spek = CandidateSpek<Boolean>().then(ValueSpek("pass"), ValueSpek("fail"))

        expectThat(spek.evaluate(true)).isEqualTo("pass")
        expectThat(spek.evaluate(false)).isEqualTo("fail")
    }

}
