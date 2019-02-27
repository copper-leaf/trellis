@file:Suppress("UNCHECKED_CAST")
package com.copperleaf.trellis.dsl.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.parser.CharInParser
import com.copperleaf.kudzu.parser.ChoiceParser
import com.copperleaf.kudzu.parser.ExpressionParser
import com.copperleaf.kudzu.parser.LazyParser
import com.copperleaf.kudzu.parser.ManyParser
import com.copperleaf.kudzu.parser.MaybeParser
import com.copperleaf.kudzu.parser.NamedParser
import com.copperleaf.kudzu.parser.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.ScanParser
import com.copperleaf.kudzu.parser.SequenceParser
import com.copperleaf.kudzu.parser.TokenParser

class TrellisDslParser {

    companion object {
        fun parse(input: String): Node? {
            val parseResult = spekExpression.test(ParserContext(input, 0, false))
            return if (parseResult != null && parseResult.second.isEmpty()) {
                parseResult.first
            } else {
                null
            }
        }

        val spekExpressionTerm = LazyParser()
        val spekExpression = LazyParser()

        private val ws = OptionalWhitespaceParser()
        private val spekNameParser = ChoiceParser(
            SequenceParser(
                CharInParser('\''),
                ScanParser(CharInParser('\'')),
                CharInParser('\''),
                name = "stringValue"
            ),
            TokenParser(),
            name = "spekName"
        )

        private val argumentValueParser = SequenceParser(
            spekExpressionTerm,
            ws
        )
        val argumentListParser = SequenceParser(
            argumentValueParser,
            MaybeParser(
                ManyParser(
                    SequenceParser(
                        CharInParser(','),
                        ws,
                        argumentValueParser,
                        ws
                    )
                )
            ),
            name = "arguments"
        )
        val argumentsParser = SequenceParser(
            CharInParser('('),
            ws,
            MaybeParser(
                argumentListParser
            ),
            ws,
            CharInParser(')')
        )
        val spekParser = SequenceParser(
            spekNameParser,
            ws,
            MaybeParser(
                argumentsParser
            )
        )

        init {
            spekExpressionTerm.parser = ChoiceParser(
                SequenceParser(
                    spekParser,
                    ws
                ),
                SequenceParser(
                    CharInParser('('),
                    ws,
                    NamedParser(spekExpression, name = "subExpr"),
                    ws,
                    CharInParser(')'),
                    ws
                ),
                name = "exprTerm"
            )
            spekExpression.parser = ExpressionParser(spekExpressionTerm, operators)
        }
    }

}