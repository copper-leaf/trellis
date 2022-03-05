package com.copperleaf.trellis.base

import com.copperleaf.trellis.util.printSpekTree

public abstract class BaseSpek<Candidate, Result>(
    final override val children: List<Spek<*, *>>
) : Spek<Candidate, Result> {

    public constructor(vararg children: Spek<*, *>) : this(children.toList())

    override val spekName: String get() = this::class.simpleName!!

    final override fun toString(): String {
        return printSpekTree()
    }
}
