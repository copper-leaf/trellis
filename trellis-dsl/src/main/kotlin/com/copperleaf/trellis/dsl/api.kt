package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.parser.ExpressionContext
import com.copperleaf.trellis.api.Spek
import kotlin.reflect.KClass

//typealias AnySpek = Spek<*, Any>
//typealias BoolSpek = Spek<*, Boolean>

data class SpekIdentifier<T : Spek<*, *>>(
    val name: String,
    val spekClass: KClass<out T>,
    val ctor: suspend (List<Spek<*, *>>) -> T
)

data class SpekExpressionContext(
    val spekClasses: List<SpekIdentifier<*>>
) : ExpressionContext<Spek<*, *>>() {

    constructor(vararg spekClasses: SpekIdentifier<*>) : this(spekClasses.toList())
}