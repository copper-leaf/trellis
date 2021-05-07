# Trellis
---

> A Kotlin implementation of the [Specification Pattern](https://en.wikipedia.org/wiki/Specification_pattern)

![GitHub release (latest by date)](https://img.shields.io/github/v/release/copper-leaf/trellis)
![Maven Central](https://img.shields.io/maven-central/v/io.github.copper-leaf/trellis-core)
![Kotlin Version](https://img.shields.io/badge/Kotlin-1.4.32-orange)

Trellis is an implementation of the [Specification Pattern](https://en.wikipedia.org/wiki/Specification_pattern)
written in Kotlin, and designed for asynchronous evaluation of specifications using Kotlin coroutines and dynamic 
creation and evaluation.

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

## Features

- [Trellis Core](https://copper-leaf.github.io/trellis/wiki/trellis-core)
  - [Java Interop](https://copper-leaf.github.io/trellis/wiki/java-interop)
  - [Introspection](https://copper-leaf.github.io/trellis/wiki/introspection)
- [Trellis DSL](https://copper-leaf.github.io/trellis/wiki/trellis-dsl)
- [Boolean Logic](https://copper-leaf.github.io/trellis/wiki/boolean-logic)
- [Numeric Logic](https://copper-leaf.github.io/trellis/wiki/numeric-logic)

## Installation

Install with Jitpack.

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation "com.github.copper-leaf.trellis:trellis-core:${trellisVersion}"
    implementation "com.github.copper-leaf.trellis:trellis-dsl:${trellisVersion}"
}
```

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

- [https://www.michael-whelan.net/rules-design-pattern](https://www.michael-whelan.net/rules-design-pattern)
- [https://www.martinfowler.com/apsupp/spec.pdf](https://www.martinfowler.com/apsupp/spec.pdf)
- [https://en.wikipedia.org/wiki/Specification_pattern](https://en.wikipedia.org/wiki/Specification_pattern)
