# Trellis
---

> A Kotlin implementation of the [Specification Pattern](https://en.wikipedia.org/wiki/Specification_pattern)

Specifikation is an implementation of the [Specification Pattern](https://en.wikipedia.org/wiki/Specification_pattern)
written in Kotlin, and designed for asynchronous evaluation of specifications using Kotlin coroutines.

## Why did it write it?

Business logic is hard, and is often quite difficult to manage consistently across an application. Furthermore, it is
difficult to evaluate business logic in the normal manner, with a bunch of `ifs`, `switches`, and so on, when the data
needed to validate these constraints are often the result of API calls or database queries which should be run 
asynchronously. The result is a mess of callbacks and spaghetti code that is not typically managed well and ends up 
being repeated throughout the application, which makes it difficult to maintain.

_Specifikation_ provides a small type-safe interface for building objects which encapsulate and validate business logic, 
and a fluent API for combining these smaller specs into larger, more complex specs. The result is that multiple 
conditions can be implemented as needed, but evaluating the complex spec is just the same as evaluating a small spec:
just pass the object to test and a callback will eventually give you the result. And since the building and testing of 
the spec is now separated from the code that needs to validate against the spec, you can now dynamically build the spec
and inject it with an IoC container, giving you a clean separation of concerns in your code.

Common use cases are for evaluating boolean logic, and for computing complex numeric logic. 

## The Spek API

A `Spek` is any object that implements the following interface:

```kotlin
interface Spek<T, U> {
    suspend fun evaluate(candidate: T): U
}
```

You'll notice that the method is marked with `suspend`, which means it is equipped to be run as a Kotlin Coroutine, and 
so building complex speks from other speks is particularly nice in Kotlin. Using this library from Java, the equivalent 
interface is similar, but a callback is used instead of a return type:

```java
public interface Spek<T, U> {
    void evaluate(T input, Continuation<U> callback);
}
```

There are 3 other important classes which may be of use when chaining speks together, the `ValueSpek`, the `ReturnSpek`, 
and the `EqualsSpek`. 

`ValueSpek` wraps a single value passed to its constructor, and the `evaluate()` method just returns that value. This is
useful for parameterizing your spec, so that thresholds can be set without changing the spec model itself, and so give
some of the more abstract speks concrete values to check against. Many of the spek extension functions include a method
which accepts either a Spek or a raw value, and the raw value is just wrapped up in a spek behind-the-scenes.

`CandidateSpek` returns the candidate directly. 

The `EqualsSpek` checks for the equality of the result of 2 other speks. If the values are both instances of `Number`, 
they are converted to `Double`s first before checking equality. All other values are compared using `.equals()`.

### Boolean Logic

One of the common use-cases of the Spek API is determining whether or not a candidate object satisfies the requirements, 
and if so, it can perform some action. An example is checking user permissions, where a given user can pass if one of
the following criteria is met:

- They have been manually granted the capability
    OR
- They are in the correct role AND this specific capability has not been revoked
    OR
- They are a super-user, and have implicit permission to do anything

If the evaluation of this spec is `true`, then the user can perform that action, otherwise they are blocked from 
performing it. 

Building such a `Spek` might look like the following:

```kotlin
val permissionSpek = HasExplicitCapabilitySpek("write")
        .or(IsRoleSpek("author").andNot(HasExplicitCapabilityRevokedSpek("write")))
        .or(IsSuperuserSpek())

val canWrite = permissionSpek.evaluate(user)
if(canWrite) {
    // perform write action
}
```

where each custom spek here is an implementation of

```kotlin
interface Spek<User, Boolean> {
    suspend fun evaluate(candidate: User): Boolean
}
```

### Numeric Logic

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

where each custom spek here is an implementation of

```kotlin
interface Spek<Customer, Number> {
    suspend fun evaluate(candidate: Customer): Number
}
```

_Note that these examples are only for demonstration and are not part of the Spek library itself, but rather you would
be able to build these speks yourself and combine them with the extensions and speks in this library to build the full
spek._

## Notes

There is some criticism over the use of the Specification pattern, as it tends be to abused and over-used, and instead
just becomes a poor replacement of the logic that is handled in the language itself. While this is certainly true if the
speks are designed to be executed synchronously, being asynchronous, as implemented in this library, gives sufficient
motivation to use this pattern over the programming language logic itself. It is difficult to cleanly capture 
conditional and complex logic asynchronously, but this library allows it to be done in a way that doesn't feel 
asyncronous. 

Furthermore, by having the desired logic encapsulated in an object, this logic can be defined and tested elsewhere and 
injected into the appropriate places with and IoC container, helping maintain a clear separation of concerns. In 
addition, it provides a structure and reusability around this kind of logic (read: maintainability) that would not exist
otherwise.  

## References

- http://www.michael-whelan.net/rules-design-pattern
- https://www.martinfowler.com/apsupp/spec.pdf
- https://en.wikipedia.org/wiki/Specification_pattern