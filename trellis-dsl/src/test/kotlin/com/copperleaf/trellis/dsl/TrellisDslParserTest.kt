package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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
        println("Parsing: [$input]")
        val output: Pair<Node, ParserContext>?
        val underTest = TrellisDslParser.argumentListParser

        output = try {
            underTest.test(input)
        } catch (e: Exception) {
            null
        }

        if (expectedSuccess) {
            expectThat(output).parsedCorrectly()
            println(output?.first?.toString())
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
        println("Parsing: [$input]")
        val output: Pair<Node, ParserContext>?
        val underTest = TrellisDslParser.argumentsParser

        output = try {
            underTest.test(input)
        } catch (e: Exception) {
            null
        }

        if (expectedSuccess) {
            expectThat(output).parsedCorrectly()
            println(output?.first?.toString())
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
        println("Parsing: [$input]")
        val output: Pair<Node, ParserContext>?
        val underTest = TrellisDslParser.spekParser

        output = try {
            underTest.test(input)
        } catch (e: Exception) {
            null
        }

        if (expectedSuccess) {
            expectThat(output).parsedCorrectly()
            println(output?.first?.toString())
        } else {
            expectThat(output).parsedIncorrectly()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "'asdf(asdf)',        true",
        "'( asdf(asdf) )',        true"
    )
    fun testParsingSpekExpressionTerm(input: String, expectedSuccess: Boolean) {
        println("Parsing: [$input]")
        val output: Pair<Node, ParserContext>?
        val underTest = TrellisDslParser.spekExpressionTerm

        output = try {
            underTest.test(input)
        } catch (e: Exception) {
            null
        }

        if (expectedSuccess) {
            expectThat(output).parsedCorrectly()
            println(output?.first?.toString())
        } else {
            expectThat(output).parsedIncorrectly()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "'asdf',                           true",
        "'asdf()',                         true",
        "'asdf(1)',                        true",
        "'asdf(1, 2)',                     true",
        "'asdf(1, 2, 3, 4, 5, 6, 7, 8)',   true",
        "'asdf(1, qwerty(2))',             true",

        "'(asdf)',                         true",
        "'(asdf(1))',                      true",
        "'(asdf(1, 2))',                   true",
        "'(asdf(1, 2, 3, 4, 5, 6, 7, 8))', true",

        "'(asdf)',                         true",
        "'(asdf(1))',                      true",
        "'(asdf(1, 2))',                   true",
        "'(asdf(1, a(b(c(d(1)))), 3, 4, 5, 6, 7, 8))', true",
        "'(asdf(1, (a(b(c(d(1))))), 3, 4, 5, 6, 7, 8))', true",
        "'(asdf(( a() ), ( a() )))', true",

        "'a and b', true",
        "'a & b',   true",

        "'a or b',  true",
        "'a | b',   true",

        "'not a',   true",
        "'!a',      true",

        "'not a and b',         true",
        "'not a or b',          true",
        "'not (a or (b & c))',  true",

        "'( a( (c and d | e(f(1, 2, 3) )) ) )', true"
    )
    fun testParsingSpekExpression(input: String, expectedSuccess: Boolean) {
        println("Parsing: [$input]")
        val output: Pair<Node, ParserContext>?
        val underTest = TrellisDslParser.spekExpression

        output = try {
            underTest.test(input)
        } catch (e: Exception) {
            null
        }

        if (expectedSuccess) {
            expectThat(output).parsedCorrectly()

            println(output.toString())

            val result = TrellisDslParser.SpekExpressionContext(emptyList())
            TrellisDslParser.visitor.visit(result, output!!.first)

            println(result.value.toString())
        } else {
            expectThat(output).parsedIncorrectly()
        }
    }

}
