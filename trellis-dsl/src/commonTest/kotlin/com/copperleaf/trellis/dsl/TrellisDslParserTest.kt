package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.dsl.parser.TrellisDslParser
import com.copperleaf.trellis.impl.math.plus
import com.copperleaf.trellis.visitor.EmptyVisitor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@Suppress("UNCHECKED_CAST")
class TrellisDslParserTest : StringSpec({
    val speks = mapOf<String, (List<Spek<*, *>>) -> Spek<*, *>>(
        "asdf" to /*            */ { ValueSpek("asdf") },
        "qwerty" to /*          */ { ValueSpek("qwerty") },
        "_t" to /*              */ { ValueSpek(true) },
        "_f" to /*              */ { ValueSpek(false) },
        "two_point_three" to /* */ { ValueSpek(2.3) },
        "one" to /*             */ { ValueSpek(1) },
        "const" to /*           */ { (a) -> a },
        "summing" to /*         */ {
            it.reduce { acc, next ->
                ((acc as Spek<Any?, Any?>) plus (next as Spek<Any?, Any?>))
            }
        },
    )

    val literalSpekTests = listOf(
        "true" to true,
        "false" to false,
        "1" to 1,
        "2.3" to 2.3,
        "'q'" to 'q',
        "'\u00A2'" to '¢',
        "'\\u00A2'" to '¢',
        "'\\uFDFD'" to '﷽',
        "\" \u00A2 \\u00A2 \\uFDFD \"" to " ¢ ¢ ﷽ ",
    )
    val namedSpekTests = listOf(
        "asdf" to "asdf",
        "qwerty" to "qwerty",
        "_t" to true,
        "_f" to false,
        "one" to 1,
        "two_point_three" to 2.3,

        "const(asdf)" to "asdf",
        "const(qwerty)" to "qwerty",
        "const(_t)" to true,
        "const(_f)" to false,
        "const(one)" to 1,
        "const(two_point_three)" to 2.3,
        "summing(one)" to 1,
        "summing(one,two_point_three)" to 3.3,
        "summing(one,two_point_three,3,4,5,6,const(7),8,9)" to 45.3,
    )
    val expressionSpekTests = listOf(
        "true and false" to false,
        "true or false" to true,
        "true and not false" to true,
        "true and (not false)" to true,

        "true & false" to false,
        "true | false" to true,
        "true & !false" to true,
        "true & (!false)" to true,

        "1 eq 1" to true,
        "1 eq 2" to false,
        "1 neq 2" to true,
        "1 lt 2" to true,
        "1 lte 2" to true,
        "1 gt 2" to false,
        "1 gte 2" to false,

        "1 == 1" to true,
        "1 == 2" to false,
        "1 != 2" to true,
        "1 < 2" to true,
        "1 <= 2" to true,
        "1 > 2" to false,
        "1 >= 2" to false,

        "2 + 4" to 6.0,
        "2 - 4" to -2.0,
        "2 * 4" to 8.0,
        "2 / 4" to 0.5,

        "(2 / 4) == (1 - (0.5))" to true,
        "((2 / 4) == (const(one) - (0.5))) eq const(_t)" to true,
    )

    val underTest = TrellisDslParser(speks)

    "testParsingLiteralSpeks" {
        literalSpekTests.forEach { (input, expectedValue) ->
            val output = underTest
                .literalSpekParser
                .parse(ParserContext.fromString(input))
            expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .also {
                    val parsedValue = it.value.evaluate(EmptyVisitor, null)
                    parsedValue shouldBe expectedValue
                }
        }
    }

    "testParsingNamedSpeks" {
        namedSpekTests.forEach { (input, expectedValue) ->
            val output = underTest
                .namedSpekParser
                .parse(ParserContext.fromString(input))
            expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .also {
                    val parsedValue = it.value.evaluate(EmptyVisitor, null)
                    parsedValue shouldBe expectedValue
                }
        }
    }

    "testParsingActualSpeks" {
        (literalSpekTests + namedSpekTests).forEach { (input, expectedValue) ->
            val output = underTest
                .actualTermParser
                .parse(ParserContext.fromString(input))
            expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .also {
                    val parsedValue = it.value.evaluate(EmptyVisitor, null)
                    parsedValue shouldBe expectedValue
                }
        }
    }

    "testParsingSpekExpression" {
        (expressionSpekTests + namedSpekTests).forEach { (input, expectedValue) ->
            val outputParserResult = underTest
                .parser
                .parse(ParserContext.fromString(input, skipWhitespace = true))
            expectThat(outputParserResult)
                .parsedCorrectly()

            val outputNode = outputParserResult
                .node()
                .isNotNull()

            val outputSpek = underTest
                .parser
                .evaluator
                .evaluate(outputNode)

            val outputValue = outputSpek.evaluate(EmptyVisitor, null)
            expectThat(outputValue) shouldBe expectedValue
        }

        (literalSpekTests).forEach { (input, expectedValue) ->
            val outputParserResult = underTest
                .parser
                .parse(ParserContext.fromString(input))
            expectThat(outputParserResult)
                .parsedCorrectly()

            val outputNode = outputParserResult
                .node()
                .isNotNull()

            val outputSpek = underTest
                .parser
                .evaluator
                .evaluate(outputNode)

            val outputValue = outputSpek.evaluate(EmptyVisitor, null)
            expectThat(outputValue) shouldBe expectedValue
        }
    }
})
