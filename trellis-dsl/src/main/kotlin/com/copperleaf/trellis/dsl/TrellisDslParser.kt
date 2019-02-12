package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.parser.CharInParser
import com.copperleaf.kudzu.parser.ChoiceParser
import com.copperleaf.kudzu.parser.EvaluableOperator
import com.copperleaf.kudzu.parser.ExpressionParser
import com.copperleaf.kudzu.parser.InfixEvaluableOperator
import com.copperleaf.kudzu.parser.LazyParser
import com.copperleaf.kudzu.parser.ManyParser
import com.copperleaf.kudzu.parser.MaybeParser
import com.copperleaf.kudzu.parser.NamedParser
import com.copperleaf.kudzu.parser.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.PrefixEvaluableOperator
import com.copperleaf.kudzu.parser.SequenceParser
import com.copperleaf.kudzu.parser.TokenParser
import com.copperleaf.kudzu.parser.WordParser
import com.copperleaf.trellis.api.AndSpek
import com.copperleaf.trellis.api.NotSpek
import com.copperleaf.trellis.api.OrSpek
import com.copperleaf.trellis.api.Spek

@Suppress("UNCHECKED_CAST")
class TrellisDslParser {

    companion object {
        fun parse(input: String): Node? {
            val parseResult = spekExpression.test(ParserContext(input, 0, false))
            return if(parseResult != null && parseResult.second.isEmpty()) {
                parseResult.first
            }
            else {
                null
            }
        }

        val spekExpressionTerm = LazyParser()
        val spekExpression = LazyParser()

        private val ws = OptionalWhitespaceParser()
        private val spekNameParser = TokenParser(name = "spekName")

        private val and = SequenceParser(
            ChoiceParser(
                WordParser("&"),
                WordParser("and")
            ),
            ws,
            name = "and"
        )
        private val or = SequenceParser(
            ChoiceParser(
                WordParser("|"),
                WordParser("or")
            ),
            ws,
            name = "or"
        )
        private val not = SequenceParser(
            ChoiceParser(
                WordParser("!"),
                WordParser("not")
            ),
            ws,
            name = "not"
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

        val operators = listOf<EvaluableOperator<Spek<*, *>>>(
            InfixEvaluableOperator(and, 40) { lhs, rhs -> AndSpek(lhs as Spek<Any, Boolean>, rhs as Spek<Any, Boolean>) },
            InfixEvaluableOperator(or, 60) { lhs, rhs -> OrSpek(lhs as Spek<Any, Boolean>, rhs as Spek<Any, Boolean>) },
            PrefixEvaluableOperator(not, 80) { lhs -> NotSpek(lhs as Spek<Any, Boolean>) }
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
                    CharInParser(')')
                ),
                name = "exprTerm"
            )
            spekExpression.parser = ExpressionParser(spekExpressionTerm, operators)
        }
    }

}