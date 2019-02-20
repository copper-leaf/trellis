package com.copperleaf.trellis.dsl

import com.copperleaf.trellis.api.EmptyVisitor
import com.copperleaf.trellis.api.LargestSpek
import com.copperleaf.trellis.api.SmallestSpek
import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.impl.strings.MaxLengthSpek
import com.copperleaf.trellis.impl.strings.MinLengthSpek
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import strikt.api.catching
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

@Suppress("UNCHECKED_CAST")
class TrellisDslVisitorTest {

    @ParameterizedTest
    @CsvSource(
        "'minLength(2) and maxLength(8)', asdf,         true",
        "'minLength(2) and maxLength(8)', a,            false",
        "'minLength(2) and maxLength(8)', asdfasdfasdf, false",

        "'minLength(8) or maxLength(4)',     asdfasdf, true",
        "'minLength(8) or maxLength(4)',     asdf,     true",
        "'minLength(8) or maxLength(4)',     asdfas,   false",
        "'minLength(8) or not maxLength(4)', asdfas,   true"
    )
    fun testParsingBooleanSpekExpression(input: String, candidate: String, expectedSuccess: Boolean) {
        val output = TrellisDslParser.parse(input)
        expectThat(output).isNotNull()

        val spekContext = SpekExpressionContext {
            register { cxt, args ->
                MinLengthSpek(args.first().typeSafe<Any, Any, String, Int>(cxt))
            }
            register { cxt, args ->
                MaxLengthSpek(args.first().typeSafe<Any, Any, String, Int>(cxt))
            }

            coerce { _, value ->
                value.toString().toInt()
            }
        }

        TrellisDslVisitor.visit(spekContext, output!!)

        val spek = spekContext.value as Spek<String, Boolean>
        expect {
            that(spek.evaluate(EmptyVisitor, candidate)).isEqualTo(expectedSuccess)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "'minLength(2) and largest(2, 5, 17)', asdf"
    )
    fun testParsingBooleanSpekExpressionTypeError(input: String, candidate: String) {
        val output = TrellisDslParser.parse(input)
        expectThat(output).isNotNull()

        val spekContext = SpekExpressionContext {
            register { cxt, args ->
                MinLengthSpek(args.first().typeSafe<Any, Any, String, Int>(cxt))
            }
            register { cxt, args ->
                val typesafeArgs = args
                    .map { it.typeSafe<Any, Any, Any, Number>(cxt) }
                    .toTypedArray()
                LargestSpek(*typesafeArgs)
            }

            coerce<Number> { _, value ->
                value.toString().toInt()
            }
        }

        TrellisDslVisitor.visit(spekContext, output!!)

        val spek = spekContext.value as Spek<String, Boolean>
        expect {
            that(catching { spek.evaluate(EmptyVisitor, candidate) })
                .isNotNull()
                .isA<ClassCastException>()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "'largest(2, 5, 17)', 'Mr. Manager', 17",
        "'largest(" +
                "largest(2, 3), " +
                "smallest(19, 17), " +
                "largest(5, 3)" +
                ")', 'Mr. Manager', 17"
    )
    fun testParsingNumericSpekExpressionWithSubExpressions(input: String, candidate: String, expectedResult: Int) {
        val output = TrellisDslParser.parse(input)
        expectThat(output).isNotNull()

        val spekContext = SpekExpressionContext {
            register { cxt, args ->
                val typesafeArgs = args
                    .map { it.typeSafe<Any, Any, Any, Number>(cxt) }
                    .toTypedArray()
                LargestSpek(*typesafeArgs)
            }
            register { cxt, args ->
                val typesafeArgs = args
                    .map { it.typeSafe<Any, Any, Any, Number>(cxt) }
                    .toTypedArray()
                SmallestSpek(*typesafeArgs)
            }

            coerce<Number> { _, value ->
                value.toString().toInt()
            }
        }

        TrellisDslVisitor.visit(spekContext, output!!)

        val spek = spekContext.value as Spek<String, Number>
        expect {
            that(spek.evaluate(EmptyVisitor, candidate)).isEqualTo(expectedResult)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "'1 > 1', false",
        "'2 > 1', true",
        "'1 >= 1', true",
        "'2 >= 1', true",

        "'1 < 1', false",
        "'1 < 2', true",
        "'1 <= 1', true",
        "'1 <= 2', true",

        "'1 == 1', true",
        "'1 != 1', false",
        "'1 == 2', false",
        "'1 != 2', true",

        "'1 gt 1', false",
        "'2 gt 1', true",
        "'1 gte 1', true",
        "'2 gte 1', true",

        "'1 lt 1', false",
        "'1 lt 2', true",
        "'1 lte 1', true",
        "'1 lte 2', true",

        "'1 eq 1', true",
        "'1 neq 1', false",
        "'1 eq 2', false",
        "'1 neq 2', true"
    )
    fun testComparisons(input: String, expectedResult: Boolean) {
        val output = TrellisDslParser.parse(input)
        expectThat(output).isNotNull()

        val spekContext = SpekExpressionContext {
            register { cxt, args ->
                val typesafeArgs = args
                    .map { it.typeSafe<Any, Any, Any, Number>(cxt) }
                    .toTypedArray()
                LargestSpek(*typesafeArgs)
            }
            register { cxt, args ->
                val typesafeArgs = args
                    .map { it.typeSafe<Any, Any, Any, Number>(cxt) }
                    .toTypedArray()
                SmallestSpek(*typesafeArgs)
            }

            coerce<Number> { _, value ->
                value.toString().toInt()
            }
        }

        TrellisDslVisitor.visit(spekContext, output!!)

        val spek = spekContext.value as Spek<String, Boolean>
        expect {
            that(spek.evaluate(EmptyVisitor, "")).isEqualTo(expectedResult)
        }
    }

}
