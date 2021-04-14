@file:Suppress("UNCHECKED_CAST")
package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.ParserException
import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.dsl.parser.TrellisDslParser
import com.copperleaf.trellis.dsl.visitor.TrellisDslVisitor
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import com.copperleaf.trellis.introspection.visitor.SpekVisitor

class TrellisDsl {

    companion object {

        fun <T, U> create(context: SpekExpressionContext, expression: String): Spek<T, U> {
            try {
                val node = TrellisDslParser.spekExpression.parse(ParserContext(expression, 0, false))

                if (node.second.isNotEmpty()) {
                    throw IllegalArgumentException("parsing failed, expected EOF, got ${node.second}")
                }

                TrellisDslVisitor.visitor.visit(context, node.first)
                return context.value as Spek<T, U>
            } catch (e: ParserException) {
                throw IllegalArgumentException("parsing failed, ${e.message}")
            }
        }

        fun <T, U> evaluate(
            context: SpekExpressionContext,
            expression: String,
            candidate: T,
            visitor: SpekVisitor = EmptyVisitor
        ): U {
            return create<T, U>(context, expression).evaluate(visitor, candidate)
        }
    }
}
