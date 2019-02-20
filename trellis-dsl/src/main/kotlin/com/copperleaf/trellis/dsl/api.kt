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
    override suspend fun evaluate(candidate: C): R {
        val unknownCandidate = coerce(bcClass, candidate) ?: default(bcClass)
        val typedCandidate: BC = unknownCandidate
        val untypedResult = base.evaluate(typedCandidate)
        val typedResult: R = coerce(rClass, untypedResult) ?: default(rClass)

        return typedResult
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


/*


eval type-safe spek
        coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
    unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
    typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)

    eval type-safe spek
            coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
        unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
        typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
        untypedResult=0.1 (br=Object)
            coercing [0.1] using {Double} to double
        typedResult=0.1 (r=Double)



    eval type-safe spek
            coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
        unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
        typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
        untypedResult=1 (br=Object)
            coercing [1] using {Integer} to integer
        typedResult=1 (r=Integer)

untypedResult=0.1 (br=Object)
    coercing [0.1] using {Number} to number
typedResult=0.1 (r=Number)



eval type-safe spek
        coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
    unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
    typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)

    eval type-safe spek
            coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
        unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
        typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
        untypedResult=0.15 (br=Object)
            coercing [0.15] using {Double} to double
        typedResult=0.15 (r=Double)

    eval type-safe spek
            coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
        unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
        typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
        untypedResult=2 (br=Object)
            coercing [2] using {Integer} to integer
        typedResult=2 (r=Integer)

untypedResult=0.0 (br=Object)
    coercing [0.0] using {Number} to number
typedResult=0.0 (r=Number)



eval type-safe spek
        coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
    unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
    typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)

    eval type-safe spek
            coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
        unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
        typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
        untypedResult=0.25 (br=Object)
            coercing [0.25] using {Double} to double
        typedResult=0.25 (r=Double)

    eval type-safe spek
            coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
        unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
        typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
        untypedResult=5 (br=Object)
            coercing [5] using {Integer} to integer
        typedResult=5 (r=Integer)

untypedResult=0.0 (br=Object)
    coercing [0.0] using {Number} to number
typedResult=0.0 (r=Number)



eval type-safe spek
        coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
    unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
    typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)

    eval type-safe spek
            coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
        unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
        typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)

        eval type-safe spek
                coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
            unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
            typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
            untypedResult=2018 (br=Object)
                coercing [2018] using {Integer} to integer
            typedResult=2018 (r=Integer)

        eval type-safe spek
                coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
            unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
            typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
            untypedResult=8 (br=Object)
                coercing [8] using {Integer} to integer
            typedResult=8 (r=Integer)

        eval type-safe spek
                coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
            unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
            typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
            untypedResult=1 (br=Object)
                coercing [1] using {Integer} to integer
            typedResult=1 (r=Integer)

    untypedResult=2018-08-01 (br=Object)
    typedResult=2018-08-01 (r=LocalDate)

    eval type-safe spek
            coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
        unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
        typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)

        eval type-safe spek
                coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
            unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
            typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
            untypedResult=2018 (br=Object)
                coercing [2018] using {Integer} to integer
            typedResult=2018 (r=Integer)

        eval type-safe spek
                coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
            unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
            typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
            untypedResult=8 (br=Object)
                coercing [8] using {Integer} to integer
            typedResult=8 (r=Integer)

        eval type-safe spek
                coercing [Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)] using {Object} to itself
            unknownCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null)
            typedCandidate=Discount(username=thatGuy01, yearsUserActive=1, couponCode=null) (bc=Object)
            untypedResult=31 (br=Object)
                coercing [31] using {Integer} to integer
            typedResult=31 (r=Integer)

    untypedResult=2018-08-31 (br=Object)
    typedResult=2018-08-31 (r=LocalDate)

untypedResult=false (br=Object)
    coercing [false] using {Number} to number

java.lang.NumberFormatException: For input string: "false"



 */