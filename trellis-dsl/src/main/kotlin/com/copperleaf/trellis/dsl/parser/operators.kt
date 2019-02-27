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
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting

val operators = listOf(

//region AND/OR/NOT
//----------------------------------------------------------------------------------------------------------------------
    createInfixOperator<Any, Boolean,  Boolean>(30,  "or", "|")  { a, b -> a || b },
    createInfixOperator<Any, Boolean,  Boolean>(40,  "and", "&") { a, b -> a && b },
    createPrefixOperator<Any, Boolean, Boolean>(130, "not", "!") { a    -> !a     },
//endregion

//region COMPARISONS
//----------------------------------------------------------------------------------------------------------------------

    createInfixOperator<Any, Any,    Boolean>(80, "eq",  "==") { a, b -> a == b },
    createInfixOperator<Any, Any,    Boolean>(80, "neq", "!=") { a, b -> a != b },
    createInfixOperator<Any, Double, Boolean>(90, "gte", ">=") { a, b -> a >= b },
    createInfixOperator<Any, Double, Boolean>(90, "gt",  ">")  { a, b -> a >  b },
    createInfixOperator<Any, Double, Boolean>(90, "lte", "<=") { a, b -> a <= b },
    createInfixOperator<Any, Double, Boolean>(90, "lt",  "<")  { a, b -> a <  b },

//endregion

//region MATH OPERATORS
//----------------------------------------------------------------------------------------------------------------------
    createInfixOperator<Any, Double, Double>(100, "plus", "+")  { a, b -> a + b },
    createInfixOperator<Any, Double, Double>(100, "minus", "-") { a, b -> a - b },
    createInfixOperator<Any, Double, Double>(110, "mul", "*")   { a, b -> a * b },
    createInfixOperator<Any, Double, Double>(110, "div", "/")   { a, b -> a / b }
//endregion

)

//region Helper functions
//----------------------------------------------------------------------------------------------------------------------

private fun createOperatorParser(name: String, vararg tokens: String): Parser {
    return SequenceParser(
        ExactChoiceParser(*tokens.map { WordParser(it) }.toTypedArray(), WordParser(name)),
        OptionalWhitespaceParser(),
        name = name
    )
}

private inline fun <reified T, reified U, reified V> createInfixOperator(priority: Int, name: String, vararg tokens: String, noinline cb: (U, U) -> V) : EvaluableOperator<SpekExpressionContext, Spek<*, *>> {
    return InfixEvaluableOperator(createOperatorParser(name, *tokens), priority) { cxt, lhs, rhs ->
        BinaryOperationSpek<T, U, V>(cxt, lhs, rhs, cb)
    }
}

class BinaryOperationSpek<T, U, V>(
    private val context: SpekExpressionContext,
    private val tClass: Class<T>,
    private val uClass: Class<U>,
    private val lhs: Spek<*, *>,
    private val rhs: Spek<*, *>,
    private val cb: (U, U) -> V
) : Spek<T, V> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): V {
        return visiting(visitor) {
            val typesafeLhs = lhs.typeSafe<Any, Any, T, U>(context, tClass, uClass)
            val typesafeRhs = rhs.typeSafe<Any, Any, T, U>(context, tClass, uClass)

            cb(typesafeLhs.evaluate(visitor, candidate), typesafeRhs.evaluate(visitor, candidate))
        }
    }

    companion object {
        inline operator fun <reified T, reified U, reified V> invoke(
            context: SpekExpressionContext,
            lhs: Spek<*, *>,
            rhs: Spek<*, *>,
            noinline cb: (U, U) -> V
        ) : BinaryOperationSpek<T, U, V> {
            return BinaryOperationSpek(
                context,
                T::class.java,
                U::class.java,
                lhs,
                rhs,
                cb
            )
        }
    }
}

private inline fun <reified T, reified U, reified V> createPrefixOperator(priority: Int, name: String, vararg tokens: String, noinline cb: (U) -> V) : EvaluableOperator<SpekExpressionContext, Spek<*, *>> {
    return PrefixEvaluableOperator(createOperatorParser(name, *tokens), priority) { cxt, base ->
        UnaryOperationSpek<T, U, V>(cxt, base, cb)
    }
}

class UnaryOperationSpek<T, U, V>(
    private val context: SpekExpressionContext,
    private val tClass: Class<T>,
    private val uClass: Class<U>,
    private val base: Spek<*, *>,
    private val cb: (U) -> V
) : Spek<T, V> {

    override val children = listOf(base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): V {
        return visiting(visitor) {
            val typesafeBase = base.typeSafe<Any, Any, T, U>(context, tClass, uClass)

            cb(typesafeBase.evaluate(visitor, candidate))
        }
    }

    companion object {
        inline operator fun <reified T, reified U, reified V> invoke(
            context: SpekExpressionContext,
            lhs: Spek<*, *>,
            noinline cb: (U) -> V
        ) : UnaryOperationSpek<T, U, V> {
            return UnaryOperationSpek(
                context,
                T::class.java,
                U::class.java,
                lhs,
                cb
            )
        }
    }
}

//endregion