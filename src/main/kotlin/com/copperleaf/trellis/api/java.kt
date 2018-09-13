package com.copperleaf.trellis.api

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun <T, U> Spek<T, U>.evaluateSync(candidate: T): U {
    return runBlocking(CommonPool) {
        this@evaluateSync.evaluate(candidate)
    }
}

fun <T, U> Spek<T, U>.evaluateAsync(candidate: T, onComplete: (U)->Unit) {
    launch {
        val result = this@evaluateAsync.evaluate(candidate)
        onComplete(result)
    }
}
