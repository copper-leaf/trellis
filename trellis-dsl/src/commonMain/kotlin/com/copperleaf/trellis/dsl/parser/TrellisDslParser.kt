package com.copperleaf.trellis.dsl.parser

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.node.mapped.ValueNode
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
import com.copperleaf.trellis.impl.math.div
import com.copperleaf.trellis.impl.comparison.eq
import com.copperleaf.trellis.impl.comparison.gt
import com.copperleaf.trellis.impl.comparison.gte
import com.copperleaf.trellis.impl.comparison.lt
import com.copperleaf.trellis.impl.comparison.lte
import com.copperleaf.trellis.impl.math.minus
import com.copperleaf.trellis.impl.booleans.not
import com.copperleaf.trellis.impl.booleans.or
import com.copperleaf.trellis.impl.math.plus
import com.copperleaf.trellis.impl.math.times

@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
class TrellisDslParser(
    private val namedSpeks: Map<String, SpekFactory>
) {

    internal val literalSpekParser: Parser<ValueNode<Spek<Any?, Any?>>> = createLiteralSpekParser()

    internal val namedSpekParser: LazyParser<ValueNode<Spek<Any?, Any?>>> = LazyParser()

    internal val actualTermParser: Parser<ValueNode<Spek<Any?, Any?>>> = createActualSpekParser()

    val parser: ExpressionParser<Spek<Any?, Any?>> = ExpressionParser(
        termParser = {
            namedSpekParser uses createNamedSpekParser(it)
            actualTermParser
        },
        operators = createOperatorsList()
    )

// Create Parsers
// ---------------------------------------------------------------------------------------------------------------------

    private fun createLiteralSpekParser(): Parser<ValueNode<Spek<Any?, Any?>>> {
        return MappedParser(
            AnyLiteralParser()
        ) {
            ValueSpek(it.value)
        }
    }

    private fun createNamedSpekParser(
        subExpressionParser: Parser<Node>
    ): Parser<ValueNode<Spek<Any?, Any?>>> {
        val subExpressionMappedParser: Parser<ValueNode<Spek<Any?, Any?>>> = MappedParser(
            subExpressionParser
        ) {
            parser.evaluator.evaluate(it)
        }

        val paramsListParser: Parser<ManyNode<ValueNode<Spek<Any?, Any?>>>> = SeparatedByParser(
            term = subExpressionMappedParser,
            separator = CharInParser(','),
        )

        val paramsListWithParenthesisParser: Parser<ValueNode<List<Spek<Any?, Any?>>>> = MappedParser(
            MaybeParser(
                SequenceParser(
                    CharInParser('('),
                    paramsListParser,
                    CharInParser(')'),
                )
            )
        ) {
            if (it.node != null) {
                val (_, paramsValues, _) = it.node!!.children
                (paramsValues as ManyNode<ValueNode<Spek<Any?, Any?>>>).nodeList.map { it.value }
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
        ) {
            val (spekName, maybeSpekArgs) = it.children

            val namedSpekFactory = namedSpeks[spekName.text]!!
            val spekFactoryArgs = (maybeSpekArgs as ValueNode<List<Spek<*, *>>>).value

            namedSpekFactory(spekFactoryArgs) as Spek<Any?, Any?>
        }
    }

    private fun createActualSpekParser(): Parser<ValueNode<Spek<Any?, Any?>>> {
        return MappedParser(
            ExactChoiceParser(
                literalSpekParser,
                namedSpekParser,
            )
        ) { (it.node as ValueNode<Spek<Any?, Any?>>).value }
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
