package com.copperleaf.trellis.dsl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.dsl.visitor.typeSafe
import com.copperleaf.trellis.impl.BetweenDatesSpek
import com.copperleaf.trellis.impl.DateSpek
import com.copperleaf.trellis.impl.IfSpek
import com.copperleaf.trellis.impl.LargestSpek
import com.copperleaf.trellis.impl.ValueSpek
import com.copperleaf.trellis.impl.plus
import com.copperleaf.trellis.impl.then
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expect
import strikt.assertions.isEqualTo
import java.time.LocalDate

@Suppress("UNCHECKED_CAST")
class TrellisReadmeNumericExpressionsTest {

    @ParameterizedTest
    @MethodSource("testArgs")
    fun testBuilderFormat(user: Discount, currentDate: LocalDate, discountAmount: Double) {
        val discountSpek = LargestSpek(
            LoyaltyDiscountSpek(discount = 0.1, yearsActive = 1),
            LoyaltyDiscountSpek(discount = 0.15, yearsActive = 2),
            LoyaltyDiscountSpek(discount = 0.25, yearsActive = 5),
            BetweenDatesSpek<Discount>(
                startDate = LocalDate.of(2018, 8, 1),
                endDate = LocalDate.of(2018, 8, 31),
                targetDate = currentDate
            ).then(
                PromotionDiscountSpek(discount = 0.20), // then spek
                ValueSpek(0.0)                          // else spek
            )
        )
            .plus(CouponDiscountSpek(discount = 0.1, couponCode = "friendsandfamily"))

        expect {
            that(discountSpek.evaluate(EmptyVisitor, user).toDouble()).isEqualTo(discountAmount, 0.01)
        }
    }

    @ParameterizedTest
    @MethodSource("testArgs")
    fun testExpressionFormat(user: Discount, currentDate: LocalDate, discountAmount: Double) {
        context.currentDate = currentDate

        expect {
            that(
                TrellisDsl.evaluate<Discount, Double>(
                    context,
                    """
                    |largest(
                    |  loyalty('0.1', 1),
                    |  loyalty('0.15', 2),
                    |  loyalty('0.25', 5),
                    |  if(
                    |    betweenDates(
                    |      date(2018, 8, 1),
                    |      date(2018, 8, 31)
                    |    ),
                    |    promotion('0.2'),
                    |    '0.0'
                    |  )
                    |) + couponCode('0.1', friendsandfamily)
                    """.trimMargin(),
                    user
                )
            ).isEqualTo(discountAmount, 0.01)
        }
    }

    companion object {

        @JvmStatic
        fun testArgs(): List<Arguments> = listOf(
            Arguments.of(Discount("thatGuy01", 1, null), LocalDate.of(2018, 1, 1), 0.1),
            Arguments.of(Discount("thatGuy01", 2, null), LocalDate.of(2018, 1, 1), 0.15),
            Arguments.of(Discount("thatGuy01", 3, null), LocalDate.of(2018, 1, 1), 0.15),
            Arguments.of(Discount("thatGuy01", 4, null), LocalDate.of(2018, 1, 1), 0.15),
            Arguments.of(Discount("thatGuy01", 5, null), LocalDate.of(2018, 1, 1), 0.25),

            Arguments.of(Discount("thatGuy01", 1, null), LocalDate.of(2018, 8, 15), 0.2),
            Arguments.of(Discount("thatGuy01", 2, null), LocalDate.of(2018, 8, 15), 0.2),
            Arguments.of(Discount("thatGuy01", 3, null), LocalDate.of(2018, 8, 15), 0.2),
            Arguments.of(Discount("thatGuy01", 4, null), LocalDate.of(2018, 8, 15), 0.2),
            Arguments.of(Discount("thatGuy01", 5, null), LocalDate.of(2018, 8, 15), 0.25),

            Arguments.of(Discount("thatGuy01", 1, "friendsandfamily"), LocalDate.of(2018, 1, 1), 0.2),
            Arguments.of(Discount("thatGuy01", 2, "friendsandfamily"), LocalDate.of(2018, 1, 1), 0.25),
            Arguments.of(Discount("thatGuy01", 3, "friendsandfamily"), LocalDate.of(2018, 1, 1), 0.25),
            Arguments.of(Discount("thatGuy01", 4, "friendsandfamily"), LocalDate.of(2018, 1, 1), 0.25),
            Arguments.of(Discount("thatGuy01", 5, "friendsandfamily"), LocalDate.of(2018, 1, 1), 0.35),

            Arguments.of(Discount("thatGuy01", 1, "friendsandfamily"), LocalDate.of(2018, 8, 15), 0.3),
            Arguments.of(Discount("thatGuy01", 2, "friendsandfamily"), LocalDate.of(2018, 8, 15), 0.3),
            Arguments.of(Discount("thatGuy01", 3, "friendsandfamily"), LocalDate.of(2018, 8, 15), 0.3),
            Arguments.of(Discount("thatGuy01", 4, "friendsandfamily"), LocalDate.of(2018, 8, 15), 0.3),
            Arguments.of(Discount("thatGuy01", 5, "friendsandfamily"), LocalDate.of(2018, 8, 15), 0.35)
        )
    }

    val context = DiscountContext {
        register { cxt, args ->
            val typesafeArgs = args
                .map { it.typeSafe<Any, Any, Any, Number>(cxt) }
                .toTypedArray()
            LargestSpek(*typesafeArgs)
        }
        register("loyalty") { cxt, args ->
            LoyaltyDiscountSpek(
                args[0].typeSafe<Any, Any, Any, Double>(cxt),
                args[1].typeSafe<Any, Any, Any, Int>(cxt)
            )
        }
        register("couponCode") { cxt, args ->
            CouponDiscountSpek(
                args[0].typeSafe<Any, Any, Any, Double>(cxt),
                args[1].typeSafe<Any, Any, Any, String>(cxt)
            )
        }
        register("promotion") { cxt, args ->
            PromotionDiscountSpek(
                args[0].typeSafe<Any, Any, Any, Double>(cxt)
            )
        }
        register("betweenDates") { cxt, args ->
            BetweenDatesSpek(
                args[0].typeSafe<Any, Any, Any, LocalDate?>(cxt),
                args[1].typeSafe<Any, Any, Any, LocalDate?>(cxt),
                ValueSpek((cxt as DiscountContext).currentDate)
            )
        }
        register("date") { cxt, args ->
            DateSpek(
                args[0].typeSafe<Any, Any, Any, Int>(cxt),
                args[1].typeSafe<Any, Any, Any, Int>(cxt),
                args[2].typeSafe<Any, Any, Any, Int>(cxt)
            )
        }
        register("if") { cxt, args ->
            IfSpek(
                args[0].typeSafe<Any, Any, Any, Boolean>(cxt),
                args[1].typeSafe<Any, Any, Any, Any>(cxt),
                args[2].typeSafe<Any, Any, Any, Any>(cxt)
            )
        }
    }

    class DiscountContext(initializer: (SpekExpressionContext.() -> Unit)? = null) :
        SpekExpressionContext(initializer) {

        var currentDate: LocalDate = LocalDate.now()

    }

    data class Discount(
        val username: String,
        val yearsUserActive: Int,
        val couponCode: String?
    )

    class LoyaltyDiscountSpek(private val discount: Spek<Any, Double>, private val yearsActive: Spek<Any, Int>) :
        Spek<Discount, Double> {
        constructor(discount: Double, yearsActive: Int) : this(ValueSpek(discount), ValueSpek(yearsActive))

        override val children = listOf(discount, yearsActive)

        override suspend fun evaluate(visitor: SpekVisitor, candidate: Discount): Double {
            return visiting(visitor) {
                val discountAmount = discount.evaluate(visitor, candidate)
                val requiredYearsActive = yearsActive.evaluate(visitor, candidate)
                if (candidate.yearsUserActive >= requiredYearsActive) discountAmount else 0.0
            }
        }
    }

    class PromotionDiscountSpek(private val discount: Spek<Any, Double>) : Spek<Discount, Double> {
        constructor(discount: Double) : this(ValueSpek(discount))

        override val children = listOf(discount)

        override suspend fun evaluate(visitor: SpekVisitor, candidate: Discount): Double {
            return visiting(visitor) { discount.evaluate(visitor, candidate) }
        }
    }

    class CouponDiscountSpek(private val discount: Spek<Any, Double>, private val couponCode: Spek<Any, String>) :
        Spek<Discount, Double> {
        constructor(discount: Double, couponCode: String) : this(ValueSpek(discount), ValueSpek(couponCode))

        override val children = listOf(discount, couponCode)

        override suspend fun evaluate(visitor: SpekVisitor, candidate: Discount): Double {
            return visiting(visitor) {
                val discountAmount = discount.evaluate(visitor, candidate)
                val requiredCouponCode = couponCode.evaluate(visitor, candidate)
                if (candidate.couponCode == requiredCouponCode) discountAmount else 0.0
            }
        }
    }

}
