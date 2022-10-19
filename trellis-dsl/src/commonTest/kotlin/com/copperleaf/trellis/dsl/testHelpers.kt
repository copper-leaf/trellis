package com.copperleaf.trellis.dsl

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.ParserResult

fun <T : Node> ParserResult<T>?.parsedCorrectly(
    expected: String? = null,
    allowRemaining: Boolean = false
): ParserResult<T> {
    if (this == null) error(
        "Subject cannot be null"
    )
    else {
        if (expected != null) {
            when (first.toString()) {
                expected.trimIndent().trim() -> {
                }
                else -> error(
                    "Output AST should be $expected, got $first"
                )
            }
        }
        if (!allowRemaining) {
            when (second.isEmpty()) {
                true -> {
                }
                else -> error("There should be nothing remaining, still had $second",)
            }
        }
    }

    return this
}

fun <T : Node> ParserResult<T>?.node(): T? = this?.first

fun <T> expectThat(value: T): T {
    return value
}

fun <T> T?.isNotNull(): T {
    return checkNotNull(this)
}
