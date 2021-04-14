@file:Suppress("UNCHECKED_CAST")
package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.parser.ExpressionContext
import com.copperleaf.trellis.api.Spek
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
    val coercionFunctions = mutableMapOf<Class<*>, (SpekExpressionContext, Any?) -> Any?>()
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

    inline fun <reified U : Any?> coerce(noinline func: (SpekExpressionContext, Any?) -> U) {
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
