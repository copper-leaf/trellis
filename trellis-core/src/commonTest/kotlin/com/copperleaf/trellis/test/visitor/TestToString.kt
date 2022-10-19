package com.copperleaf.trellis.test.visitor

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.LazySpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.impl.conditionals.then
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestToString : StringSpec({

    "testValueSpekToString" {
        val spek = ValueSpek<Unit, String>("pass")

        println(spek.toString())

        spek.toString() shouldBe """
            |(pass)
            """.trimMargin()
    }

    "testIfSpekToString" {
        val spek = CandidateSpek<Boolean>().then(ValueSpek("pass"), ValueSpek("fail"))

        println(spek.toString())

        spek.toString() shouldBe """
            |(IfSpek:
            |  (CandidateSpek)
            |  (pass)
            |  (fail)
            |)
            """.trimMargin()
    }

    "testRecursiveSpekToString" {
        val ifSpek = LazySpek<Boolean, String>()

        ifSpek uses CandidateSpek<Boolean>().then(ValueSpek("pass"), ifSpek)

        println(ifSpek.toString())

        ifSpek.toString() shouldBe """
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
    }
})
