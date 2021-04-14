package com.copperleaf.trellis.introspection.visitor

import com.copperleaf.trellis.api.Spek

fun <T, U> Spek<T, U>.match(candidate: T, onStart: (SpekMatcher<U>.() -> Unit)? = null): SpekMatcher<U> {
    val matcher = SpekMatcher<U>()

    onStart?.invoke(matcher)

    val innerVisitor = MatchingVisitor(matcher)

    innerVisitor.exploring = true
    exploring(innerVisitor) {
        this.children.forEach { it.explore(innerVisitor) }
    }

    innerVisitor.exploring = false
    matcher.onExplorationCompleteCallback?.invoke(innerVisitor.matches)
    val result = this.evaluate(innerVisitor, candidate)

    matcher.result = result
    matcher.matches = innerVisitor.matches

    return matcher
}

class MatchingVisitor(
    private val matcher: SpekMatcher<*>
) : SpekVisitor {

    private var depth: Int = 0
    internal var exploring: Boolean = true
    internal val matches = mutableListOf<SpekMatch>()

    override fun enter(candidate: Spek<*, *>) {
        depth++
    }

    override fun <U> leave(candidate: Spek<*, *>, result: U) {
        if (exploring) {
            if (matcher.matchFilter == null || matcher.matchFilter!!.invoke(candidate)) {
                matches.add(
                    SpekMatch(candidate, depth, candidate.children.isEmpty()).also {
                        matcher.onNodeFoundCallback?.invoke(it)
                    }
                )
            }
        } else {
            if (matcher.matchFilter == null || matcher.matchFilter!!.invoke(candidate)) {
                matches.single { it.spek === candidate }.also {
                    it.hit = true
                    it.result = result
                    matcher.onNodeHitCallback?.invoke(it)
                }
            }
        }

        depth--
    }
}

data class SpekMatch(
    val spek: Spek<*, *>,
    val depth: Int,
    val isTerminal: Boolean,
    var hit: Boolean = false,
    var result: Any? = null
)

class SpekMatcher<U> {

    var result: U? = null
    var matches: List<SpekMatch>? = null

    internal var onNodeFoundCallback: ((SpekMatch) -> Unit)? = null
    internal var onExplorationCompleteCallback: ((List<SpekMatch>) -> Unit)? = null
    internal var onNodeHitCallback: ((SpekMatch) -> Unit)? = null
    internal var matchFilter: ((Spek<*, *>) -> Boolean)? = null

    fun onNodeFound(cb: (SpekMatch) -> Unit) {
        onNodeFoundCallback = cb
    }

    fun onExplorationComplete(cb: (List<SpekMatch>) -> Unit) {
        onExplorationCompleteCallback = cb
    }

    fun onNodeHit(cb: (SpekMatch) -> Unit) {
        onNodeHitCallback = cb
    }

    fun filter(cb: (Spek<*, *>) -> Boolean) {
        matchFilter = cb
    }
}
