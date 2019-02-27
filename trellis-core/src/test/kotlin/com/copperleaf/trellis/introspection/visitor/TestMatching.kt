package com.copperleaf.trellis.introspection.visitor

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.impl.MaxLengthSpek
import com.copperleaf.trellis.impl.MinLengthSpek
import com.copperleaf.trellis.impl.NamedSpek
import com.copperleaf.trellis.impl.and
import com.copperleaf.trellis.impl.named
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.isTrue

class TestMatching {

    lateinit private var spek: Spek<String, Boolean>
    private var onUpdateCallsMade = 0

    @BeforeEach
    internal fun setUp() {
        spek = MinLengthSpek(6).named("MinLength") and MaxLengthSpek(12).named("MaxLength")
        onUpdateCallsMade = 0
    }

    @Test
    fun testMatching_allSpeksHit() = runBlocking<Unit> {
        val input = "asdfasdf"

        val matchResult = spek.match(input) {
            filter { it is NamedSpek }
            onUpdate { onUpdateCallsMade++ }
        }

        expectThat(matchResult.matches)
            .isNotNull()
            .isNotEmpty()
            .hasSize(2)
            .and { this[0]
                .and { get { hit }.isTrue() }
                .and { get { result }.isNotNull().isEqualTo(true) }
            }
            .and { this[1]
                .and { get { hit }.isTrue() }
                .and { get { result }.isNotNull().isEqualTo(true) }
            }
            .and { get {
                forEach { println(it) }
            } }

        expectThat(onUpdateCallsMade)
            .isEqualTo(2)
    }

    @Test
    fun testMatching_notAllSpeksHit() = runBlocking<Unit> {
        val input = "asdf"

        val matchResult = spek.match(input) {
            filter { it is NamedSpek }
            onUpdate { onUpdateCallsMade++ }
        }

        expectThat(matchResult.matches)
            .isNotNull()
            .isNotEmpty()
            .hasSize(2)
            .and {
                this[0].and {
                    get { hit }.isTrue()
                    get { result }.isNotNull().isEqualTo(false)
                }
            }
            .and {
                this[1].and {
                    get { hit }.isFalse()
                    get { result }.isNull()
                }
            }
            .and { get {
                forEach { println(it) }
            } }

        expectThat(onUpdateCallsMade)
            .isEqualTo(1)
    }

}