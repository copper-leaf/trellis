package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.Visitor
import com.copperleaf.kudzu.child
import com.copperleaf.kudzu.find
import com.copperleaf.kudzu.has
import com.copperleaf.kudzu.hasChild
import com.copperleaf.kudzu.parser.CharInParser
import com.copperleaf.kudzu.parser.ChoiceNode
import com.copperleaf.kudzu.parser.ChoiceParser
import com.copperleaf.kudzu.parser.EvaluableOperator
import com.copperleaf.kudzu.parser.ExpressionContext
import com.copperleaf.kudzu.parser.ExpressionParser
import com.copperleaf.kudzu.parser.ExpressionVisitor
import com.copperleaf.kudzu.parser.InfixEvaluableOperator
import com.copperleaf.kudzu.parser.LazyParser
import com.copperleaf.kudzu.parser.ManyNode
import com.copperleaf.kudzu.parser.ManyParser
import com.copperleaf.kudzu.parser.MaybeNode
import com.copperleaf.kudzu.parser.MaybeParser
import com.copperleaf.kudzu.parser.NamedNode
import com.copperleaf.kudzu.parser.NamedParser
import com.copperleaf.kudzu.parser.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.PrefixEvaluableOperator
import com.copperleaf.kudzu.parser.SequenceNode
import com.copperleaf.kudzu.parser.SequenceParser
import com.copperleaf.kudzu.parser.TokenParser
import com.copperleaf.kudzu.parser.WordNode
import com.copperleaf.kudzu.parser.WordParser
import com.copperleaf.trellis.api.AndSpek
import com.copperleaf.trellis.api.NotSpek
import com.copperleaf.trellis.api.OrSpek
import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.api.ValueSpek

typealias AnySpek = Spek<*, *>
typealias BoolSpek = Spek<Any, Boolean>

class TrellisDslParser {

    data class SpekIdentifier(
        val name: String,
        val spekClasses: Class<AnySpek>,
        val ctor: (List<AnySpek>) -> AnySpek
    )

    data class SpekExpressionContext(val spekClasses: List<SpekIdentifier>) : ExpressionContext<AnySpek>()

    companion object {
        private val and: Parser
        private val or: Parser
        private val not: Parser

        private val ws: Parser
        private val argumentValueParser: Parser
        val argumentListParser: Parser
        val argumentsParser: Parser

        private val spekNameParser: Parser
        val spekParser: Parser
        val spekExpressionTerm: LazyParser
        val spekExpression: LazyParser

        val visitor: Visitor<SpekExpressionContext>

        init {
            spekExpressionTerm = LazyParser()
            spekExpression = LazyParser()

            ws = OptionalWhitespaceParser()
            argumentValueParser = SequenceParser(
                spekExpressionTerm,
                ws
            )
            argumentListParser = SequenceParser(
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

            argumentsParser = SequenceParser(
                CharInParser('('),
                ws,
                MaybeParser(
                    argumentListParser
                ),
                ws,
                CharInParser(')')
            )

            spekNameParser = TokenParser(name = "spekName")
            spekParser = SequenceParser(
                spekNameParser,
                ws,
                MaybeParser(
                    argumentsParser
                )
            )

            and = SequenceParser(
                ChoiceParser(
                    WordParser("&"),
                    WordParser("and"),
                    name = "and"
                ),
                ws
            )
            or = SequenceParser(
                ChoiceParser(
                    WordParser("|"),
                    WordParser("or"),
                    name = "or"
                ),
                ws
            )
            not = SequenceParser(
                ChoiceParser(
                    WordParser("!"),
                    WordParser("not"),
                    name = "not"
                ),
                ws
            )

            val operators = listOf<EvaluableOperator<AnySpek>>(
                InfixEvaluableOperator(and, 40) { lhs, rhs -> AndSpek(lhs as BoolSpek, rhs as BoolSpek) },
                InfixEvaluableOperator(or, 60) { lhs, rhs -> OrSpek(lhs as BoolSpek, rhs as BoolSpek) },
                PrefixEvaluableOperator(not, 80) { lhs -> NotSpek(lhs as BoolSpek) }
            )

            spekExpressionTerm.parser = ChoiceParser(
                spekParser,
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

            visitor = ExpressionVisitor(operators, ::createSpekFromExpressionOrNode)
        }

        private fun createSpekFromExpressionOrNode(context: SpekExpressionContext, node: Node): AnySpek {
            val hasSubExpr = node
                .child<SequenceNode>()
                .has(NamedNode::class, "subExpr")

            return if(hasSubExpr) {
                createSpekFromExpression(context, node)
            }
            else {
                createSpekFromTerm(context, node)
            }
        }

        private fun createSpekFromExpression(context: SpekExpressionContext, node: Node): AnySpek {
            val subExpr = node
                .child<SequenceNode>()
                .find<NamedNode>("subExpr")
                .child()
            val innerContext = context.copy()
            TrellisDslParser.visitor.visit(innerContext, subExpr)

            return innerContext.value!!
        }

        private fun createSpekFromTerm(context: SpekExpressionContext, node: Node): AnySpek {
            val argValues = mutableListOf<AnySpek>()

            val spekName = node
                .child<SequenceNode>()
                .find<WordNode>("spekName")
                .text

            val spekArgsNode = node
                .child<SequenceNode>()
                .child<MaybeNode>()

            if (spekArgsNode.hasChild()) {
                val argsListNode = spekArgsNode.child<SequenceNode>().child<MaybeNode>()

                if (argsListNode.hasChild()) {
                    val initialArgNode = argsListNode
                        .child<SequenceNode>()
                        .child<SequenceNode>()
                        .find<ChoiceNode>("exprTerm")
                    val initialArgSpek = createSpekFromExpressionOrNode(context, initialArgNode)
                    argValues.add(initialArgSpek)

                    val otherArgs = argsListNode
                        .child<SequenceNode>()
                        .child<MaybeNode>()

                    if (otherArgs.hasChild()) {
                        val manyArgs = otherArgs
                            .child<ManyNode>()
                            .children

                        for (otherArg in manyArgs) {
                            val otherArgNode = otherArg
                                .child<SequenceNode>()
                                .find<ChoiceNode>("exprTerm")
                            val otherArgSpek = createSpekFromExpressionOrNode(context, otherArgNode)
                            argValues.add(otherArgSpek)
                        }
                    }
                }
            }

            return createSpekFromArgs(context, spekName, argValues)
        }

        private fun createSpekFromArgs(context: SpekExpressionContext, spekName: String, args: List<AnySpek>): AnySpek {
            return context
                .spekClasses
                .firstOrNull { it.name == spekName }
                ?.ctor
                ?.invoke(args)
                ?: ValueSpek<Any, String>(spekName)
        }
    }


}