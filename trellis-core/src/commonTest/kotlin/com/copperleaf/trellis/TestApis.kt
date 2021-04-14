package com.copperleaf.trellis

import kotlin.test.*

fun <T> expectThat(value: T): T {
    return value
}

fun <T : Any> expectCatching(value: () -> T): Pair<T?, Throwable?> {
    try {
        val evaluated = value()
        return evaluated to null
    } catch (t: Throwable) {
        return null to t
    }
}

fun <T : Any> Pair<T?, Throwable?>.isFailure(): Throwable {
    if (second != null) {
    } else {
        error("Expected $this to be failure, but was success")
    }

    return second!!
}

fun <T : Any> Pair<T?, Throwable?>.isSuccess(): T {
    if (first != null) {
    } else {
        error("Expected $this to be success, but was failure")
    }

    return first!!
}

fun <T> List<T>.containsExactlyInAnyOrder(vararg items: T): List<T> {
    check(this == listOf(*items))

    return this
}

fun <T> List<T>.listIsNotEmpty(): List<T> {
    check(this.size > 0)

    return this
}

fun <T> List<T>.hasSize(size: Int): List<T> {
    assertEquals(size, this.size)

    return this
}

fun <T, U> T.get(block: T.() -> U): U {
    return block()
}

fun <T> T.isEqualTo(other: Any?): T {
    assertEquals(other, this)

    return this
}

fun <T> T.isSameInstanceAs(other: Any?): T {
    check(this === other)

    return this
}

fun Boolean.isFalse() {
    assertEquals(false, this)
}

fun <T> T?.isNotNull(): T {
    return checkNotNull(this)
}

fun <T> T?.isNull(): T? {
    check(this == null)

    return this
}

fun Boolean.isTrue() {
    assertEquals(true, this)
}
