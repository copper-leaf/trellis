@file:Suppress("UNCHECKED_CAST")

package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.parser.ExpressionContext
import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.api.SpekVisitor
import com.copperleaf.trellis.api.visiting
import kotlin.reflect.KClass


data class SpekIdentifier<T : Spek<*, *>>(
    val name: String,
    val spekClass: KClass<out T>,
    val ctor: (SpekExpressionContext, List<Spek<Any, Any>>) -> T
)

open class SpekExpressionContext(
    initializer: (SpekExpressionContext.() -> Unit)? = null
) : ExpressionContext<Spek<*, *>>() {

    val spekIdentifiers = mutableListOf<SpekIdentifier<*>>()
    val coercionFunctions = mutableMapOf<Class<*>, (SpekExpressionContext, Any) -> Any>()
    val defaultFunctions = mutableMapOf<Class<*>, (SpekExpressionContext) -> Any>()

    init {
        initializer?.invoke(this)
    }

    inline fun <reified S : Spek<*, *>> register(
        name: String = S::class.java.simpleName.removeSuffix("Spek").decapitalize(),
        noinline func: (SpekExpressionContext, List<Spek<Any, Any>>) -> S
    ) {
        spekIdentifiers.add(SpekIdentifier(name, S::class, func))
    }

    inline fun <reified U : Any> coerce(noinline func: (SpekExpressionContext, Any) -> U) {
        coercionFunctions[U::class.java] = func
    }

    inline fun <reified U : Any> default(noinline func: (SpekExpressionContext) -> U) {
        defaultFunctions[U::class.java] = func
    }

    fun copy(): SpekExpressionContext {
        val base = this
        return SpekExpressionContext {
            spekIdentifiers.clear()
            spekIdentifiers.addAll(base.spekIdentifiers)

            coercionFunctions.clear()
            coercionFunctions.putAll(base.coercionFunctions)

            defaultFunctions.clear()
            defaultFunctions.putAll(base.defaultFunctions)
        }
    }

}

class TypeSafeSpek<BC, BR, C, R>(
    private val context: SpekExpressionContext,
    private val bcClass: Class<BC>,
    private val brClass: Class<BR>,
    private val cClass: Class<C>,
    private val rClass: Class<R>,
    private val base: Spek<BC, BR>
) : Spek<C, R> {

    override val children = listOf(base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: C): R {
        return visiting(visitor) {
            val unknownCandidate = coerce(bcClass, candidate) ?: default(bcClass)
            val typedCandidate: BC = unknownCandidate
            val untypedResult = base.evaluate(visitor, typedCandidate)
            val typedResult: R = coerce(rClass, untypedResult) ?: default(rClass)

            typedResult
        }
    }

    private fun <T, U> coerce(
        toClass: Class<U>,
        input: T
    ): U? {
        return if ((input as Any).javaClass == toClass) {
            input as U
        } else if (context.coercionFunctions.containsKey(toClass)) {
            return context.coercionFunctions[toClass]!!.invoke(context, input as Any) as U
        } else {
            when (toClass) {
                String::class.java                               -> {
                    input.toString() as U
                }
                Int::class.java, Integer::class.java             -> {
                    input.toString().toInt() as U?
                }
                Double::class.java, java.lang.Double::class.java -> {
                    input.toString().toDouble() as U?
                }
                Number::class.java, java.lang.Number::class.java -> {
                    input.toString().toDouble() as U?
                }
                else                                             -> {
                    input as? U?
                }
            }
        }
    }

    private fun <U> default(
        toClass: Class<U>
    ): U {
        return if (context.defaultFunctions.containsKey(toClass)) {
            return context.defaultFunctions[toClass]!!.invoke(context) as U
        } else {
            when (toClass) {
                String::class.java  -> "" as U
                Int::class.java     -> 0 as U
                Integer::class.java -> 0 as U
                Any::class.java     -> object {} as U
                else                -> throw IllegalArgumentException("cannot get default value for ${toClass.name}")
            }
        }
    }

    override fun toString(): String {
        return "TypeSafeSpek Converts from ${base.javaClass.simpleName}<${bcClass.simpleName}, ${brClass.simpleName}> to TypeSafeSpek<${cClass.simpleName}, ${rClass.simpleName}>)"
    }

}

inline fun <reified BC, reified BR, reified C, reified R> Spek<*, *>.typeSafe(context: SpekExpressionContext): Spek<C, R> {
    return TypeSafeSpek(
        context,
        BC::class.java,
        BR::class.java,
        C::class.java,
        R::class.java,
        this as Spek<BC, BR>
    )
}


