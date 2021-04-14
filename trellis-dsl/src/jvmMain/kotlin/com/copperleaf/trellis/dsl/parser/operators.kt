@file:Suppress("UNCHECKED_CAST")

package com.copperleaf.trellis.dsl.parser

import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.parser.EvaluableOperator
import com.copperleaf.kudzu.parser.ExactChoiceParser
import com.copperleaf.kudzu.parser.InfixEvaluableOperator
import com.copperleaf.kudzu.parser.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.PrefixEvaluableOperator
import com.copperleaf.kudzu.parser.SequenceParser
import com.copperleaf.kudzu.parser.WordParser
import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.dsl.SpekExpressionContext
import com.copperleaf.trellis.dsl.visitor.typeSafe
import com.copperleaf.trellis.impl.BinaryOperationSpek
import com.copperleaf.trellis.impl.UnaryOperationSpek

val operators = listOf(

//region AND/OR/NOT
// ---------------------------------------------------------------------------------------------------------------------
    createInfixOperator<Any?, Boolean, Boolean>(30, "or", "|") { a, b -> a || b },
    createInfixOperator<Any?, Boolean, Boolean>(40, "and", "&") { a, b -> a && b },
    createPrefixOperator<Any?, Boolean, Boolean>(130, "not", "!") { a -> !a },
//endregion

//region COMPARISONS
// ---------------------------------------------------------------------------------------------------------------------

    createInfixOperator<Any?, Any, Boolean>(80, "eq", "==") { a, b -> a == b },
    createInfixOperator<Any?, Any, Boolean>(80, "neq", "!=") { a, b -> a != b },
    createInfixOperator<Any?, Double, Boolean>(90, "gte", ">=") { a, b -> a >= b },
    createInfixOperator<Any?, Double, Boolean>(90, "gt", ">") { a, b -> a > b },
    createInfixOperator<Any?, Double, Boolean>(90, "lte", "<=") { a, b -> a <= b },
    createInfixOperator<Any?, Double, Boolean>(90, "lt", "<") { a, b -> a < b },

//endregion

//region MATH OPERATORS
// ---------------------------------------------------------------------------------------------------------------------
    createInfixOperator<Any?, Double, Double>(100, "plus", "+") { a, b -> a + b },
    createInfixOperator<Any?, Double, Double>(100, "minus", "-") { a, b -> a - b },
    createInfixOperator<Any?, Double, Double>(110, "mul", "*") { a, b -> a * b },
    createInfixOperator<Any?, Double, Double>(110, "div", "/") { a, b -> a / b }
//endregion

)

//region Helper functions
// ---------------------------------------------------------------------------------------------------------------------

private fun createOperatorParser(name: String, vararg tokens: String): Parser {
    return SequenceParser(
        ExactChoiceParser(*tokens.map { WordParser(it) }.toTypedArray(), WordParser(name)),
        OptionalWhitespaceParser(),
        name = name
    )
}

private inline fun <reified CandidateType, reified CoercedType, reified ResultType> createInfixOperator(
    priority: Int,
    name: String,
    vararg tokens: String,
    noinline cb: (CoercedType, CoercedType) -> ResultType
): EvaluableOperator<SpekExpressionContext, Spek<*, *>> {
    return InfixEvaluableOperator(createOperatorParser(name, *tokens), priority) { cxt, lhs, rhs ->
        val tClass = CandidateType::class.java
        val uClass = CoercedType::class.java
        val typesafeLhs = lhs.typeSafe<Any?, Any, CandidateType, CoercedType>(cxt, tClass, uClass)
        val typesafeRhs = rhs.typeSafe<Any?, Any, CandidateType, CoercedType>(cxt, tClass, uClass)

        BinaryOperationSpek(typesafeLhs, typesafeRhs, "$name(${tokens.joinToString()})") { a, b -> cb(a(), b()) }
    }
}

private inline fun <reified CandidateType, reified CoercedType : Any, reified ResultType> createPrefixOperator(
    priority: Int,
    name: String,
    vararg tokens: String,
    noinline cb: (CoercedType) -> ResultType
): EvaluableOperator<SpekExpressionContext, Spek<*, *>> {
    return PrefixEvaluableOperator(createOperatorParser(name, *tokens), priority) { cxt, base ->
        val tClass = CandidateType::class.java
        val uClass = CoercedType::class.java
        val typesafeBase = base.typeSafe<Any?, Any, CandidateType, CoercedType>(cxt, tClass, uClass)

        UnaryOperationSpek(typesafeBase, "$name(${tokens.joinToString()})") { a -> cb(a()) }
    }
}

//endregion
