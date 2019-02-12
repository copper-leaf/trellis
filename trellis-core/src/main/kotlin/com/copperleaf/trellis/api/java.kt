package com.copperleaf.trellis.api

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun <T, U> Spek<T, U>.evaluateSync(candidate: T): U {
    return runBlocking {
        this@evaluateSync.evaluate(candidate)
    }
}

fun <T, U> Spek<T, U>.evaluateAsync(candidate: T, onComplete: (U)->Unit) {
    GlobalScope.launch {
        val result = this@evaluateAsync.evaluate(candidate)
        onComplete(result)
    }
}
