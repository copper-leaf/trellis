
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