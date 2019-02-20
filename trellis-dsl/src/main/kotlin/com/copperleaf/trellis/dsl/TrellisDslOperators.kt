package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.parser.EvaluableOperator
import com.copperleaf.kudzu.parser.ExactChoiceParser
import com.copperleaf.kudzu.parser.InfixEvaluableOperator
import com.copperleaf.kudzu.parser.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.PrefixEvaluableOperator
import com.copperleaf.kudzu.parser.SequenceParser
import com.copperleaf.kudzu.parser.WordParser
import com.copperleaf.trellis.api.AddSpek
import com.copperleaf.trellis.api.AndSpek
import com.copperleaf.trellis.api.DivideSpek
import com.copperleaf.trellis.api.EqualsOperatorSpek
import com.copperleaf.trellis.api.GreaterThanOperatorSpek
import com.copperleaf.trellis.api.LessThanOperatorSpek
import com.copperleaf.trellis.api.MultiplySpek
import com.copperleaf.trellis.api.NotSpek
import com.copperleaf.trellis.api.OrSpek
import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.api.SubtractSpek
import com.copperleaf.trellis.api.not

internal typealias AnySpek = Spek<Any, Any>
internal typealias BoolSpek = Spek<Any, Boolean>
internal typealias NumSpek = Spek<Number, Number>

@Suppress("UNCHECKED_CAST")
class TrellisDslOperators {


    companion object {
        val operators = listOf<EvaluableOperator<SpekExpressionContext, Spek<*, *>>>(

//region AND/OR/NOT
//----------------------------------------------------------------------------------------------------------------------
            InfixEvaluableOperator(createOperatorParser("or", "|"), 30) { cxt, lhs, rhs ->
                OrSpek(
                    lhs.typeSafe<Any, Any, Any, Boolean>(cxt),
                    rhs.typeSafe<Any, Any, Any, Boolean>(cxt)
                )
            },
            InfixEvaluableOperator(createOperatorParser("and", "&"), 40) { cxt, lhs, rhs ->
                AndSpek(
                    lhs.typeSafe<Any, Any, Any, Boolean>(cxt),
                    rhs.typeSafe<Any, Any, Any, Boolean>(cxt)
                )
            },
            PrefixEvaluableOperator(createOperatorParser("not", "!"), 130) { cxt, lhs ->
                NotSpek(
                    lhs.typeSafe<Any, Any, Any, Boolean>(cxt)
                )
            },
//endregion

//region COMPARISONS
//----------------------------------------------------------------------------------------------------------------------

            InfixEvaluableOperator(createOperatorParser("eq", "=="), 80) { _, lhs, rhs ->
                EqualsOperatorSpek(
                    lhs as AnySpek,
                    rhs as AnySpek,
                    false
                )
            },
            InfixEvaluableOperator(createOperatorParser("neq", "!="), 80) { _, lhs, rhs ->
                EqualsOperatorSpek(
                    lhs as AnySpek,
                    rhs as AnySpek,
                    false
                ).not()
            },
            InfixEvaluableOperator(createOperatorParser("gte", ">="), 90) { cxt, lhs, rhs ->
                GreaterThanOperatorSpek(
                    lhs.typeSafe<Any, Any, Any, Number>(cxt),
                    rhs.typeSafe<Any, Any, Any, Number>(cxt),
                    true
                )
            },
            InfixEvaluableOperator(createOperatorParser("gt", ">"), 90) { cxt, lhs, rhs ->
                GreaterThanOperatorSpek(
                    lhs.typeSafe<Any, Any, Any, Number>(cxt),
                    rhs.typeSafe<Any, Any, Any, Number>(cxt),
                    false
                )
            },
            InfixEvaluableOperator(createOperatorParser("lte", "<="), 90) { cxt, lhs, rhs ->
                LessThanOperatorSpek(
                    lhs.typeSafe<Any, Any, Any, Number>(cxt),
                    rhs.typeSafe<Any, Any, Any, Number>(cxt),
                    true
                )
            },
            InfixEvaluableOperator(createOperatorParser("lt", "<"), 90) { cxt, lhs, rhs ->
                LessThanOperatorSpek(
                    lhs.typeSafe<Any, Any, Any, Number>(cxt),
                    rhs.typeSafe<Any, Any, Any, Number>(cxt),
                    false
                )
            },
//endregion

//region MATH OPERATORS
//----------------------------------------------------------------------------------------------------------------------
            InfixEvaluableOperator(createOperatorParser("plus", "+"), 100) { cxt, lhs, rhs ->
                AddSpek(
                    lhs.typeSafe<Any, Any, Any, Number>(cxt),
                    rhs.typeSafe<Any, Any, Any, Number>(cxt)
                )
            },
            InfixEvaluableOperator(createOperatorParser("minus", "-"), 100) { cxt, lhs, rhs ->
                SubtractSpek(
                    lhs.typeSafe<Any, Any, Any, Number>(cxt),
                    rhs.typeSafe<Any, Any, Any, Number>(cxt)
                )
            },
            InfixEvaluableOperator(createOperatorParser("mul", "*"), 110) { cxt, lhs, rhs ->
                MultiplySpek(
                    lhs.typeSafe<Any, Any, Any, Number>(cxt),
                    rhs.typeSafe<Any, Any, Any, Number>(cxt)
                )
            },
            InfixEvaluableOperator(createOperatorParser("div", "/"), 110) { cxt, lhs, rhs ->
                DivideSpek(
                    lhs.typeSafe<Any, Any, Any, Number>(cxt),
                    rhs.typeSafe<Any, Any, Any, Number>(cxt)
                )
            }
//endregion

        )

        private fun createOperatorParser(name: String, vararg tokens: String): Parser {
            return SequenceParser(
                ExactChoiceParser(*tokens.map { WordParser(it) }.toTypedArray(), WordParser(name)),
                OptionalWhitespaceParser(),
                name = name
            )
        }
    }

}
