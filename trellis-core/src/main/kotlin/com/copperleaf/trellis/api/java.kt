package com.copperleaf.trellis.api

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.NotNull

fun <T, U> Spek<T, U>.evaluateSync(@NotNull visitor: SpekVisitor, candidate: T): U {
    return runBlocking {
        this@evaluateSync.evaluate(visitor, candidate)
    }
}

fun <T, U> Spek<T, U>.evaluateAsync(@NotNull visitor: SpekVisitor, candidate: T, onComplete: (U)->Unit) {
    GlobalScope.launch {
        val result = this@evaluateAsync.evaluate(visitor, candidate)
        onComplete(result)
    }
}
