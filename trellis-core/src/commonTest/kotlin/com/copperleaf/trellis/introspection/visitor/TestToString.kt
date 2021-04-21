package com.copperleaf.trellis.introspection.visitor

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.LazySpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.expectThat
import com.copperleaf.trellis.impl.conditionals.then
import com.copperleaf.trellis.isEqualTo
import kotlin.test.Test

class TestToString {

    @Test
    fun testValueSpekToString() {
        val spek = ValueSpek<Unit, String>("pass")

        println(spek.toString())

        expectThat(spek.toString()).isEqualTo(
            """
            |(pass)
            """.trimMargin()
        )
    }

    @Test
    fun testIfSpekToString() {
        val spek = CandidateSpek<Boolean>().then(ValueSpek("pass"), ValueSpek("fail"))

        println(spek.toString())

        expectThat(spek.toString()).isEqualTo(
            """
            |(IfSpek:
            |  (CandidateSpek)
            |  (pass)
            |  (fail)
            |)
            """.trimMargin()
        )
    }

    @Test
    fun testRecursiveSpekToString() {
        val ifSpek = LazySpek<Boolean, String>()

        ifSpek uses CandidateSpek<Boolean>().then(ValueSpek("pass"), ifSpek)

        println(ifSpek.toString())

        expectThat(ifSpek.toString()).isEqualTo(
            """
            |(IfSpek:
            |  (IfSpek:
            |    (CandidateSpek)
            |    (pass)
            |    (IfSpek:
            |      ...recursive call
            |    )
            |  )
            |)
            """.trimMargin()
        )
    }
}
