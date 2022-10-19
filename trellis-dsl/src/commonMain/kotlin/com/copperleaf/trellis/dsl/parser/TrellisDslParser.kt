package com.copperleaf.trellis.dsl.parser

import com.copperleaf.kudzu.node.expression.RootExpressionNode
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.choice.ExactChoiceParser
import com.copperleaf.kudzu.parser.expression.ExpressionParser
import com.copperleaf.kudzu.parser.expression.Operator
import com.copperleaf.kudzu.parser.lazy.LazyParser
import com.copperleaf.kudzu.parser.many.SeparatedByParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.parser.value.AnyLiteralParser
import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.impl.booleans.and
import com.copperleaf.trellis.impl.booleans.not
import com.copperleaf.trellis.impl.booleans.or
import com.copperleaf.trellis.impl.comparison.eq
import com.copperleaf.trellis.impl.comparison.gt
import com.copperleaf.trellis.impl.comparison.gte
import com.copperleaf.trellis.impl.comparison.lt
import com.copperleaf.trellis.impl.comparison.lte
import com.copperleaf.trellis.impl.math.div
import com.copperleaf.trellis.impl.math.minus
import com.copperleaf.trellis.impl.math.plus
import com.copperleaf.trellis.impl.math.times

@Suppress("UNCHECKED_CAST")
public class TrellisDslParser(
    private val namedSpeks: Map<String, SpekFactory>
) {

    internal val literalSpekParser: Parser<SpekNode> = createLiteralSpekParser()

    internal val namedSpekParser: LazyParser<SpekNode> = LazyParser()

    internal val actualTermParser: Parser<SpekNode> = createActualSpekParser()

    public val parser: ExpressionParser<Spek<Any?, Any?>> = ExpressionParser(
        termParser = {
            namedSpekParser uses createNamedSpekParser(it)
            actualTermParser
        },
        operators = createOperatorsList()
    )

// Create Parsers
// ---------------------------------------------------------------------------------------------------------------------

    private fun createLiteralSpekParser(): Parser<SpekNode> {
        return MappedParser(
            AnyLiteralParser()
        ) {
            ValueSpek(it.value)
        }
    }

    private fun createNamedSpekParser(
        subExpressionParser: Parser<RootExpressionNode>
    ): Parser<SpekNode> {
        val subExpressionMappedParser: Parser<SpekNode> = MappedParser(
            subExpressionParser
        ) {
            parser.evaluator.evaluate(it)
        }

        val paramsListParser: Parser<ManyNode<SpekNode>> = SeparatedByParser(
            term = subExpressionMappedParser,
            separator = CharInParser(','),
        )

        val paramsListWithParenthesisParser: Parser<SpekListNode> = MappedParser(
            MaybeParser(
                SequenceParser(
                    CharInParser('('),
                    paramsListParser,
                    CharInParser(')'),
                )
            )
        ) {
            if (it.node != null) {
                val (_, _, paramsValues, _) = it.node!!
                paramsValues.nodeList.map { it.value }
            } else {
                emptyList()
            }
        }

        return MappedParser(
            SequenceParser(
                ExactChoiceParser(
                    namedSpeks.keys.map { LiteralTokenParser(it) }
                ),
                paramsListWithParenthesisParser
            )
        ) { (_, spekName, maybeSpekArgs) ->
            val namedSpekFactory = namedSpeks[spekName.text]!!
            val spekFactoryArgs = maybeSpekArgs.value

            namedSpekFactory(spekFactoryArgs) as Spek<Any?, Any?>
        }
    }

    private fun createActualSpekParser(): Parser<SpekNode> {
        return MappedParser(
            ExactChoiceParser(
                literalSpekParser,
                namedSpekParser,
            )
        ) { (it.node as SpekNode).value }
    }

    private fun createOperatorsList(): List<Operator<Spek<Any?, Any?>>> {
        return listOf(
            Operator.Infix("or", 30, aliases = listOf("|")) { a, b -> (a or b) as Spek<Any?, Any?> },
            Operator.Infix("and", 40, aliases = listOf("&")) { a, b -> (a and b) as Spek<Any?, Any?> },
            Operator.Prefix("not", 130, aliases = listOf("!")) { a -> (a.not()) as Spek<Any?, Any?> },

            Operator.Infix("eq", 80, aliases = listOf("==")) { a, b -> (a eq b) as Spek<Any?, Any?> },
            Operator.Infix("neq", 80, aliases = listOf("!=")) { a, b -> (a eq b).not() as Spek<Any?, Any?> },
            Operator.Infix("gte", 90, aliases = listOf(">=")) { a, b -> (a gte b) as Spek<Any?, Any?> },
            Operator.Infix("gt", 90, aliases = listOf(">")) { a, b -> (a gt b) as Spek<Any?, Any?> },
            Operator.Infix("lte", 90, aliases = listOf("<=")) { a, b -> (a lte b) as Spek<Any?, Any?> },
            Operator.Infix("lt", 90, aliases = listOf("<")) { a, b -> (a lt b) as Spek<Any?, Any?> },

            Operator.Infix("plus", 100, aliases = listOf("+")) { a, b -> (a + b) as Spek<Any?, Any?> },
            Operator.Infix("minus", 100, aliases = listOf("-")) { a, b -> (a - b) as Spek<Any?, Any?> },
            Operator.Infix("mul", 110, aliases = listOf("*")) { a, b -> (a * b) as Spek<Any?, Any?> },
            Operator.Infix("div", 110, aliases = listOf("/")) { a, b -> (a / b) as Spek<Any?, Any?> },
        )
    }
}
