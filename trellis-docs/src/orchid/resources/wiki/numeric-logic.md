---
---

Another use-case is computing the value for a user with many branching conditions and complex logic to decide whether 
a given condition should be applied. An example is computing the discount for a customer on an online store, which may
factor in customer loyalty, timely promotions, and coupon codes. The computation will look for the greatest possible 
discount of all those available, and apply that one at checkout:

- 10% Loyalty discount after 1 year
- 15% Loyalty discount after 2 years
- 25% Loyalty discount after 5 years
- 20% off during August for back-to-school promotion
- Additional 10% off for friends and family with coupon code

Building such a `Spek` might look like the following:

**Specification Builder**

```kotlin
val discountSpek = LargestSpek(
        LoyaltyDiscountSpek(discount=0.1, 1),
        LoyaltyDiscountSpek(discount=0.15, 2),
        LoyaltyDiscountSpek(discount=0.25, 5),
        BetweenDatesSpek(LocalDate.of(2018, 8, 1), LocalDate.of(2018, 8, 31))
            .then(
                PromotionDiscount(discount=0.20), // then spek
                ValueSpek(0)                      // else spek
            )
)
.plus(CouponDiscountSpek(discount=0.1, code="friendsandfamily"))

val discount = discountSpek.evaluate(customer)
checkout(customer, cart, discount)
```

Or, using the {{ anchor('Trellis DSL') }}:

**Specification Expression**
```
largest(
  loyalty('0.1', 1),
  loyalty('0.15', 2),
  loyalty('0.25', 5),
  if(
    betweenDates(
      date(2018, 8, 1),
      date(2018, 8, 31)
    ),
    promotion('0.2'),
    '0.0'
  )
) + couponCode('0.1', friendsandfamily)
```

**Configuration**

```kotlin
val context = SpekExpressionContext {
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
val discount = TrellisDsl.evaluate<Discount, Double>(
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
    customer
)
checkout(customer, cart, discount)
```
