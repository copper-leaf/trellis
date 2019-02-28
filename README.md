# Trellis
---

> A Kotlin implementation of the [Specification Pattern](https://en.wikipedia.org/wiki/Specification_pattern)

[![Build Status](https://travis-ci.org/copper-leaf/trellis.svg?branch=master)](https://travis-ci.org/copper-leaf/trellis)
[![Codacy Project Grade](https://api.codacy.com/project/badge/Grade/ededa933de9e47059050db93071f8d09)](https://www.codacy.com/app/cjbrooks12/trellis?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=copper-leaf/trellis&amp;utm_campaign=Badge_Grade)
[![Code Coverage](https://api.codacy.com/project/badge/Coverage/ededa933de9e47059050db93071f8d09)](https://www.codacy.com/app/cjbrooks12/trellis?utm_source=github.com&utm_medium=referral&utm_content=copper-leaf/trellis&utm_campaign=Badge_Coverage)

Specifikation is an implementation of the [Specification Pattern](https://en.wikipedia.org/wiki/Specification_pattern)
written in Kotlin, and designed for asynchronous evaluation of specifications using Kotlin coroutines.

## Why did it write it?

Business logic is hard, and is often quite difficult to manage consistently across an application. Furthermore, it is
difficult to evaluate business logic in the normal manner, with a bunch of `ifs`, `switches`, and so on, when the data
needed to validate these constraints are often the result of API calls or database queries which should be run 
asynchronously. The result is a mess of callbacks and spaghetti code that is not typically managed well and ends up 
being repeated throughout the application, which makes it difficult to maintain.

_Trellis_ provides a small type-safe interface for building objects which encapsulate and validate business logic, and a 
fluent API for combining these smaller specs into larger, more complex specs. The result is that multiple conditions can 
be implemented as needed, but evaluating the complex spec is just the same as evaluating a small spec: just pass the 
object to test and a callback will eventually give you the result. And since the building and testing of the spec is now 
separated from the code that needs to validate against the spec, you can now dynamically build the spec and inject it 
with an IoC container, giving you a clean separation of concerns in your code.

Common use cases are for evaluating boolean logic, and for computing complex numeric logic. 

## The Spek API

A `Spek` is any object that implements the following interface:

```kotlin
interface Spek<T, U> {
    suspend fun evaluate(visitor: SpekVisitor, candidate: T): U
}
```

You'll notice that the method is marked with `suspend`, which means it is equipped to be run as a Kotlin Coroutine, and 
so building complex speks from other speks is particularly nice in Kotlin. Using this library from Java, the equivalent 
interface is similar, but a callback is used instead of a return type:

```java
public interface Spek<T, U> {
    void evaluate(SpekVisitor visitor, T input, Continuation<U> callback);
}
```

There are 3 other important classes which may be of use when chaining speks together, the `ValueSpek`, the 
`CandidateSpek`, and the `EqualsSpek`. 

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

## Java Interoperability

Trellis uses Kotlin coroutines to implement all speks so that all evaluation is async by default. But because Kotlin 
coroutines are a _compile-time_ feature of `kotlinc`, they cannot be used directly by Java code. However, Trellis ships 
with a couple helper functions that bridge the gap between the world of Kotlin coroutines and the world of Java.

You can evaluate a result synchronously with `JavaKt.evaluateSync(spek, candidate)`. This will _block_ the calling 
thread until the coroutine completes, using the coroutine function `runBlocking` internally, returning the result 
syncronously.

```java
double discount = JavaKt.evaluateSync(discountSpek, customer);
checkout(customer, cart, discount)
```

You can also evaluate a result asynchronously with `JavaKt.evaluateAync(spek, candidate, callback)`. Internally, this 
will use `launch` to evaluate the coroutine _without blocking_ the calling thread. When the result is ready, it will 
call the callback function with the evaluated result. Note that, due to how Kotlin implements callbacks, you must return
`null` with a type of `Unit` from your handler function.

```java
JavaKt.evaluateAsync(discountSpek, customer, (discount) -> {
    checkout(customer, cart, discount);
    return null;
});
```

You can also create and use `Spek`s in Java code much the same as in Kotlin code. The Java interface looks slightly
different from the Kotlin interface, but you can ignore the extra `Continuation` parameter and just return the value
as needed. The actual Kotlin compiler implementation of the coroutine is significantly more complex when it comes to 
evaluating other `Spek`s from within a `Spek`, so you really should only implement a `Spek` in Java if it is only 
returning a value directly. 

For example, the `ValueSpek` directly returns a single value, and it's Java equivalent looks like this:

```java
public final class ValueSpek<T, U> implements Spek<T, U> {
    private final U value;

    public ValueSpek(final U value) {
        this.value = value;
    }

    @Override
    public Object evaluate(T candidate, @NotNull Continuation<? super U> continuation) {
        return value;
    }
}
```

## Introspection

### SpekVisitors

Until now, we have not mentioned the first parameter of the `Spek.evaluate(...)` method. It is a 
[GOF Visitor](https://en.wikipedia.org/wiki/Visitor_pattern) that is passed through all Speks as they do their 
processing, allowing you to receive events as the Spek tree is evaluated. Here is the full Visitor API:

```kotlin
interface SpekVisitor {
    fun enter(candidate: Spek<*, *>)
    fun <U> leave(candidate: Spek<*, *>, result: U)
}
```

The visitor receives the `enter()` event as the very first call in every Spek's `evaluate()` function, and `leave()` as
the very last call. In both cases you receive the Spek firing the event, and when leaving you also receive the value it
is returning, so you can inspect the state of sub-trees of the full Spek object.

There are a few handy Visitors built-in for your use: the `EmptyVisitor` singleton, the `PrintVisitor` class, and the
`VisitorFilter` class.

`EmptyVisitor` does nothing, simply discarding the events as they are received.

`PrintVisitor` will log the events and track the nesting of each Spek, showing a nicely-formatted tree. By default it 
logs to `System.out`, but on creation you can configure it to any `PrintStream` to capture the output.

`VisitorFilter` accepts another Visitor and a callback predicate as parameters. Only if the predicate succeeds will the 
event be passed through to the Visitor it wraps, so you can filter out inner nodes and things like that, for example.

The usage of any of these is the same: `val result = spek.evaluate(visitor, candidate)` 

### SpekMatcher

A natural extension of the Visitor API is to use it to determine which nodes were actually hit during the process of 
evaluating the tree. For example, a boolean expression will fail-fast and ignore sub-trees of the Spek, but it would be
nice to _know_ which sub-trees were not evaluated. Matchers will provide you with this information.

Is essentially has two steps: the first step "explores" the Spek tree, recursively locating the children of each Spek 
until all nodes have been explored. Then, as the Spek is being evaluated, a Visitor can match the events received with
the nodes that it previously explored. After the evaluation is complete, you will be left with a `SpekMatcher` 
containing a list of all Nodes that were discovered, and related metadata about whether it was evaluated or not and its
result. 

Usage looks like the following. Basically, instead of calling `spek.evaluate(...)`, call the `spek.match()` extension
method. Instead of returning the result, you will receive back the `SpekMatcher`:

```kotlin
val matchResult = spek.match(input) {
    filter { it.children.isEmpty() } // only match leaf nodes
    onNodeFound { }                  // Receive an additional event whenever a node is discovered in the exploration phase
    onNodeHit   { }                  // Receive an additional event whenever a node is hit in the evaluation phase
}
val result = matchResult.result // the value returned from the Spek
val matches = matchResult.matches // a list of matched Speks and metadata
```

## Dynamic Speks (spek-dsl)

The core Trellis library is perfect for building your specifications in a type-safe way, but being integrated into the 
programming language means that changes to Speks mean recompiling your code. But in some cases, it may be desirable to 
build and evaluate Speks dynamically, so that they may be changes via configuration files or APIs.

Trellis has an additional module, `trellis-dsl`, that provides exactly this functionality. It has a minimal 
Specification language for building and evaluating speks in a fluent, easy-to-read format. Let's look at the two 
examples above, rewritten using the Spek DSL:

### Boolean Logic

**Specification Expression**
```
cap(write) or (role(author) and not capRevoked(write)) or superuser()
```

**Configuration**

```kotlin
val context = SpekExpressionContext {
    register("role") { cxt, args ->
        IsRoleSpek(args.first().typeSafe<Any, Any, Any, String>(cxt))
    }
    register("superuser") { _, _ ->
        IsSuperuserSpek()
    }
    register("cap") { cxt, args ->
        HasExplicitCapabilitySpek(args.first().typeSafe<Any, Any, Any, String>(cxt))
    }
    register("capRevoked") { cxt, args ->
        HasExplicitCapabilityRevokedSpek(args.first().typeSafe<Any, Any, Any, String>(cxt))
    }
}
TrellisDsl.evaluate<User, Boolean>(
    context,
    "cap(write) or (role(author) and not capRevoked(write)) or superuser()",
    user
)
```

### Numeric Logic

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
```

Note how this last example uses nested speks. They will be parsed and evaluated in that same, nested tree structure, and
there is no limit to the depth of sub-speks.

### DSL Configuration

In order to have the DSl create the leaf nodes from your specification objects, you'll need a `SpekExpressionContext`, 
and you'll register your objects with its `register` method. That method accepts a lambda that receives a list of Speks
parsed from its parameters. You will typically need to coerce these untyped speks to the types needed by your custom 
speks, and you can use the `typeSafe` method to wrap them and coerce the parameters passed through the tree as 
necessary. 

The `typeSafe` method is a bit verbose because you need to pass it 4 type parameters, which are, in the following order:

- `BC` - The "candidate" type for the spek being wrapped (the base spek). Or, the type being passed into the wrapped spek.
- `BR` - The "return" type for the base spek. Or, the type being returned from the wrapped spek, that your spek will 
    consume
- `C` - The "candidate" type for your custom spek. Or, the type being passed into your custom spek, that must be coerced 
    into the type passed to the base spek
- `R` - The "return" type for your custom spek. Or, the type being that the base spek's return type must be coerced to in
    order to be used by your custom spek
    
You cal also register custom coercion functions in the context using the `coerce` function, and the type to return if 
coercion fails with the `default` function.

## Notes

There is some criticism over the use of the Specification pattern, as it tends be to abused and over-used, and instead
just becomes a poor replacement of the logic that is handled in the language itself. While this is certainly true if the
speks are designed to be executed synchronously, being asynchronous, as implemented in this library, gives sufficient
motivation to use this pattern over the programming language logic itself. It is difficult to cleanly capture 
conditional and complex logic asynchronously, but this library allows it to be done in a way that doesn't feel 
asynchronous. 

Furthermore, by having the desired logic encapsulated in an object, this logic can be defined and tested elsewhere and 
injected into the appropriate places with and IoC container, helping maintain a clear separation of concerns. In 
addition, it provides a structure and reusability around this kind of logic (read: maintainability) that would not exist
otherwise. 

And lastly, by providing a DSL, Speks can be created dynamically, at runtime, without recompiling your code. 

## References

- http://www.michael-whelan.net/rules-design-pattern
- https://www.martinfowler.com/apsupp/spec.pdf
- https://en.wikipedia.org/wiki/Specification_pattern