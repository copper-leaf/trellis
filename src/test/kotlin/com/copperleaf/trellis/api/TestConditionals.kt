package com.copperleaf.trellis.api

import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isEqualTo

class TestConditionals {

    @Test
    fun testIf() = runBlocking<Unit> {
        val spek = CandidateSpek<Boolean>().then(ValueSpek("pass"), ValueSpek("fail"))

        expect(spek.evaluate(true)).isEqualTo("pass")
        expect(spek.evaluate(false)).isEqualTo("fail")
    }

}
