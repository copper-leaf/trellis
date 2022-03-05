# Trellis

> A Kotlin implementation of the [Specification Pattern](https://en.wikipedia.org/wiki/Specification_pattern)

![GitHub release (latest by date)](https://img.shields.io/github/v/release/copper-leaf/trellis)
![Maven Central](https://img.shields.io/maven-central/v/io.github.copper-leaf/trellis-core)
![Kotlin Version](https://img.shields.io/badge/Kotlin-1.6.10-orange)

Trellis is an implementation of the [Specification Pattern](https://en.wikipedia.org/wiki/Specification_pattern)
written in Kotlin, and designed for asynchronous evaluation of specifications using Kotlin coroutines and dynamic 
creation and evaluation.

# Supported Platforms/Features

| Platform |
| -------- |
| Android  |
| JVM      |
| iOS      |
| JS       |

# Installation

```kotlin
repositories {
    mavenCentral()
}

// for plain JVM or Android projects
dependencies {
    implementation("io.github.copper-leaf:trellis-core:{{site.version}}")
    implementation("io.github.copper-leaf:trellis-dsl:{{site.version}}")
}

// for multiplatform projects
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.copper-leaf:trellis-core:{{site.version}}")
                implementation("io.github.copper-leaf:trellis-dsl:{{site.version}}")
            }
        }
    }
}
```

# Documentation

See the [website](https://copper-leaf.github.io/trellis/) for detailed documentation and usage instructions.

# License

Trellis is licensed under the BSD 3-Clause License, see [LICENSE.md](https://github.com/copper-leaf/trellis/tree/main/LICENSE.md).

# References

- [https://www.michael-whelan.net/rules-design-pattern](https://www.michael-whelan.net/rules-design-pattern)
- [https://www.martinfowler.com/apsupp/spec.pdf](https://www.martinfowler.com/apsupp/spec.pdf)
- [https://en.wikipedia.org/wiki/Specification_pattern](https://en.wikipedia.org/wiki/Specification_pattern)
