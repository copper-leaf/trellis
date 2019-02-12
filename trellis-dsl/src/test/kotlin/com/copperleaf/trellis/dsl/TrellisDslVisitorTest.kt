package com.copperleaf.trellis.dsl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.api.ValueSpek
import com.copperleaf.trellis.impl.strings.MaxLengthSpek
import com.copperleaf.trellis.impl.strings.MinLengthSpek
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

@Suppress("UNCHECKED_CAST")
class TrellisDslVisitorTest {

    @ParameterizedTest
    @CsvSource(
        "'minLength(2) and maxLength(8)', asdf,         true",
        "'minLength(2) and maxLength(8)', a,            false",
        "'minLength(2) and maxLength(8)', asdfasdfasdf, false",

        "'minLength(8) or maxLength(4)',  asdfasdf,     true",
        "'minLength(8) or maxLength(4)',  asdf,         true",
        "'minLength(8) or maxLength(4)',  asdfas,       false"
    )
    fun testParsingSpekExpression(input: String, candidate: String, expectedSuccess: Boolean) {
        val output = TrellisDslParser.parse(input)
        expectThat(output).isNotNull()

        val spekContext = SpekExpressionContext(
            SpekIdentifier("minLength", MinLengthSpek::class) { args ->
                if(args.isNotEmpty()) {
                    val spek: Spek<Any, Any> = args.first() as Spek<Any, Any>
                    val eval = spek.evaluate(candidate)
                    val intVal = eval.toString().toIntOrNull() ?: 2
                    MinLengthSpek(ValueSpek(intVal))
                }
                else {
                    MinLengthSpek(ValueSpek(2))
                }
            },
            SpekIdentifier("maxLength", MaxLengthSpek::class) { args ->
                if(args.isNotEmpty()) {
                    val spek: Spek<Any, Any> = args.first() as Spek<Any, Any>
                    val eval = spek.evaluate(candidate)
                    val intVal = eval.toString().toIntOrNull() ?: 2
                    MaxLengthSpek(ValueSpek(intVal))
                }
                else {
                    MaxLengthSpek(ValueSpek(2))
                }
            }
        )
        TrellisDslVisitor.visit(spekContext, output!!)

        val spek = spekContext.value as Spek<String, Boolean>
        expect {
            that(spek.evaluate(candidate)).isEqualTo(expectedSuccess)
        }
    }

}
