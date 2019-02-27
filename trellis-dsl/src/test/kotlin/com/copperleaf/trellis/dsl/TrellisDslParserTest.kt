package com.copperleaf.trellis.dsl

import com.copperleaf.trellis.dsl.parser.TrellisDslParser
import com.copperleaf.trellis.dsl.visitor.TrellisDslVisitor
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat

class TrellisDslParserTest {

    @ParameterizedTest
    @CsvSource(
        "'asdf',        true",
        "'asdf,asdf',   true",
        "'asdf, asdf',  true",
        "'asdf ,asdf',  true",
        "'asdf , asdf', true",
        "' asdf ,asdf', false", // cannot lead with whitespace
        "'asdf ,asdf ', true",
        "'1, 2, 5',     true",

        "',',           false",
        "' ',           false",
        "'',            false"
    )
    fun testParsingArgumentList(input: String, expectedSuccess: Boolean) {
        val output = TrellisDslParser.argumentListParser.test(input)

        if (expectedSuccess) {
            expectThat(output).parsedCorrectly()
        } else {
            expectThat(output).parsedIncorrectly()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "'(asdf)',        true",
        "'(asdf,asdf)',   true",
        "'(asdf, asdf)',  true",
        "'(asdf ,asdf)',  true",
        "'(asdf , asdf)', true",
        "'(1, 2, 5)',     true",

        "'()',            true",
        "'( )',           true",
        "'(,)',           false",
        "'( ,)',          false",
        "'(, )',          false"
    )
    fun testParsingArguments(input: String, expectedSuccess: Boolean) {
        val output = TrellisDslParser.argumentsParser.test(input)

        if (expectedSuccess) {
            expectThat(output).parsedCorrectly()
        } else {
            expectThat(output).parsedIncorrectly()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "'asdf(asdf)',        true",
        "'asdf(asdf,asdf)',   true",
        "'asdf(asdf, asdf)',  true",
        "'asdf(asdf ,asdf)',  true",
        "'asdf(asdf , asdf)', true",
        "'asdf(1, 2, 5)',     true",

        "'asdf()',            true",
        "'asdf( )',           true",
        "'asdf(,)',           false",
        "'asdf( ,)',          false",
        "'asdf(, )',          false",

        "'asdf()',            true",
        "'asdf ()',           true",
        "'asdf() ',           false",
        "' asdf()',           false"
    )
    fun testParsingSpekValue(input: String, expectedSuccess: Boolean) {
        val output = TrellisDslParser.spekParser.test(input)

        if (expectedSuccess) {
            expectThat(output).parsedCorrectly()
        } else {
            expectThat(output).parsedIncorrectly()
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "asdf(asdf)",
            "(asdf(asdf))"
        ]
    )
    fun testParsingSpekExpressionTerm(input: String) {
        val output = TrellisDslParser.spekExpressionTerm.test(input)
        expectThat(output).parsedCorrectly()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "asdf",
            "asdf()",
            "asdf(1)",
            "asdf(1, 2)",
            "asdf(1, 2, 3, 4, 5, 6, 7, 8)",
            "asdf(1, qwerty(2))",
            "(asdf)",
            "(asdf(1))",
            "(asdf(1, 2))",
            "(asdf(1, 2, 3, 4, 5, 6, 7, 8))",
            "(asdf)",
            "(asdf(1))",
            "(asdf(1, 2))",
            "(asdf(1, a(b(c(d(1)))), 3, 4, 5, 6, 7, 8))",
            "(asdf(1, (a(b(c(d(1))))), 3, 4, 5, 6, 7, 8))",
            "(asdf(( a() ), ( a() )))",
            "a and b",
            "a & b",
            "a or b",
            "a | b",
            "not a",
            "!a",
            "not a and b",
            "not a or b",
            "not (a or (b & c))",
            "( a( (c and d | e(f(1, 2, 3) )) ) )",

            "a()and b",
            "a and b()",
            "a()and b()",
            "a() and b",
            "a and b()",
            "a() and b()"
        ]
    )
    fun testParsingSpekExpression(input: String) {
        val output = TrellisDslParser.spekExpression.test(input)
        expectThat(output).parsedCorrectly()

        val result = SpekExpressionContext()
        TrellisDslVisitor.visit(result, output!!.first)
    }

}
