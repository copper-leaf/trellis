package com.copperleaf.trellis.introspection.visitor

import com.copperleaf.trellis.api.Spek

interface SpekVisitor {
    fun enter(candidate: Spek<*, *>)
    fun <U> leave(candidate: Spek<*, *>, result: U)
}