@file:Suppress("UNCHECKED_CAST")

package com.copperleaf.trellis.dsl.visitor

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.dsl.SpekExpressionContext
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting

class TypeSafeSpek<BC, BR, C, R>(
    private val context: SpekExpressionContext,
    private val bcClass: Class<BC>,
    private val brClass: Class<BR>,
    private val cClass: Class<C>,
    private val rClass: Class<R>,
    private val base: Spek<BC?, BR>
) : Spek<C, R> {

    override val children = listOf(base)

    override fun evaluate(visitor: SpekVisitor, candidate: C): R {
        return visiting(visitor) {
            val unknownCandidate = coerce(bcClass, candidate) ?: default(bcClass, candidate == null)
            val typedCandidate: BC? = unknownCandidate
            val untypedResult = base.evaluate(visitor, typedCandidate)
            val typedResult: Any? = (coerce(rClass, untypedResult) ?: default(rClass))

            if (typedResult == null || rClass.isAssignableFrom(typedResult::class.java)) {
                typedResult as R
            } else {
                throw ClassCastException(
                    "expected ${rClass.name} value. " +
                        "$base returned type ${typedResult::class.java.simpleName} " +
                        "with value $typedResult"
                )
            }
        }
    }

    private fun <T, U> coerce(
        toClass: Class<U>,
        input: T
    ): U? {
        return if ((input as Any?)?.javaClass == toClass) {
            input as U
        } else if (context.coercionFunctions.containsKey(toClass)) {
            return context.coercionFunctions[toClass]!!.invoke(context, input as Any) as U
        } else {
            when (toClass) {
                String::class.java -> {
                    input.toString() as U
                }
                Boolean::class.java -> {
                    input.toString().toBoolean() as U?
                }
                Int::class.java, Integer::class.java -> {
                    input.toString().toInt() as U?
                }
                Double::class.java, java.lang.Double::class.java -> {
                    input.toString().toDouble() as U?
                }
                Number::class.java, java.lang.Number::class.java -> {
                    input.toString().toDouble() as U?
                }
                else -> {
                    input as? U?
                }
            }
        }
    }

    private fun <U> default(
        toClass: Class<U>,
        nullable: Boolean
    ): U? {
        return if (context.defaultFunctions.containsKey(toClass)) {
            return context.defaultFunctions[toClass]!!.invoke(context) as U
        } else if (nullable) {
            null
        } else {
            when (toClass) {
                String::class.java -> "" as U
                Boolean::class.java -> false as U
                Int::class.java -> 0 as U
                Integer::class.java -> 0 as U
                Any::class.java -> object {} as U
                else -> throw IllegalArgumentException("cannot get default value for ${toClass.name}")
            }
        }
    }

    private fun <U> default(
        toClass: Class<U>
    ): U {
        return default(toClass, false)!!
    }

    override fun toString(): String {
        return "TypeSafeSpek (Converts from " +
            "${base.javaClass.simpleName}<${bcClass.simpleName}, ${brClass.simpleName}> to " +
            "TypeSafeSpek<${cClass.simpleName}, ${rClass.simpleName}>)"
    }
}

inline fun <reified BC, reified BR, reified C, reified R> Spek<*, *>.typeSafe(
    context: SpekExpressionContext
): Spek<C, R> {
    return TypeSafeSpek(
        context,
        BC::class.java,
        BR::class.java,
        C::class.java,
        R::class.java,
        this as Spek<BC?, BR>
    )
}

inline fun <reified BC, reified BR, C, R> Spek<*, *>.typeSafe(
    context: SpekExpressionContext,
    cClass: Class<C>,
    rClass: Class<R>
): Spek<C, R> {
    return TypeSafeSpek(
        context,
        BC::class.java,
        BR::class.java,
        cClass,
        rClass,
        this as Spek<BC?, BR>
    )
}
