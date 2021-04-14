package com.copperleaf.trellis.introspection.visitor

import com.copperleaf.trellis.*
import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.impl.MaxLengthSpek
import com.copperleaf.trellis.impl.MinLengthSpek
import com.copperleaf.trellis.impl.NamedSpek
import com.copperleaf.trellis.impl.and
import com.copperleaf.trellis.impl.named
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestMatching {

    private lateinit var spek: Spek<String, Boolean>
    private var onNodeFoundUpdateCallsMade = 0
    private var onNodeHitUpdateCallsMade = 0

    @BeforeTest
    internal fun setUp() {
        spek = MinLengthSpek(6).named("MinLength") and MaxLengthSpek(12).named("MaxLength")
        onNodeFoundUpdateCallsMade = 0
        onNodeHitUpdateCallsMade = 0
    }

    @Test
    fun testMatching_allSpeksHit() {
        val input = "asdfasdf"

        val matchResult = spek.match(input) {
            filter { it is NamedSpek }
            onNodeFound { onNodeFoundUpdateCallsMade++ }
            onNodeHit { onNodeHitUpdateCallsMade++ }
        }

        expectThat(matchResult.matches)
            .isNotNull()
            .listIsNotEmpty()
            .hasSize(2)
            .also {
                it[0].hit.isTrue()
                it[0].result.isNotNull().isEqualTo(true)
            }
            .also {
                it[1].hit.isTrue()
                it[1].result.isNotNull().isEqualTo(true)
            }
            .also {
                it.forEach { println(it) }
            }

        expectThat(onNodeFoundUpdateCallsMade)
            .isEqualTo(2)
        expectThat(onNodeHitUpdateCallsMade)
            .isEqualTo(2)
    }

    @Test
    fun testMatching_notAllSpeksHit() {
        val input = "asdf"

        val matchResult = spek.match(input) {
            filter { it is NamedSpek }
            onNodeFound { onNodeFoundUpdateCallsMade++ }
            onNodeHit { onNodeHitUpdateCallsMade++ }
        }

        expectThat(matchResult.matches)
            .isNotNull()
            .listIsNotEmpty()
            .hasSize(2)
            .also {
                it[0].hit.isTrue()
                it[0].result.isNotNull().isEqualTo(false)
            }
            .also {
                it[1].hit.isFalse()
                it[1].result.isNull()
            }
            .also {
                it.forEach { println(it) }
            }

        expectThat(onNodeFoundUpdateCallsMade)
            .isEqualTo(2)
        expectThat(onNodeHitUpdateCallsMade)
            .isEqualTo(1)
    }
}
