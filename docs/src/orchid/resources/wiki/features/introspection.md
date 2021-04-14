### SpekVisitors

Until now, we have not mentioned the first parameter of the `Spek.evaluate(...)` method. It is a 
[GOF Visitor](https://en.wikipedia.org/wiki/Visitor_pattern) that is passed through all Speks as they do their 
processing, allowing you to receive events as the Spek tree is evaluated. Here is the full Visitor API:

```kotlin
interface SpekVisitor {
    fun enter(candidate: Spek<*, *>)
    fun <U> leave(candidate: Spek<*, *>, result: U)
}
```

The visitor receives the `enter()` event as the very first call in every Spek's `evaluate()` function, and `leave()` as
the very last call. In both cases you receive the Spek firing the event, and when leaving you also receive the value it
is returning, so you can inspect the state of sub-trees of the full Spek object.

There are a few handy Visitors built-in for your use: the `EmptyVisitor` singleton, the `PrintVisitor` class, and the
`VisitorFilter` class.

`EmptyVisitor` does nothing, simply discarding the events as they are received.

`PrintVisitor` will log the events and track the nesting of each Spek, showing a nicely-formatted tree. By default it 
logs to `System.out`, but on creation you can configure it to any `PrintStream` to capture the output.

`VisitorFilter` accepts another Visitor and a callback predicate as parameters. Only if the predicate succeeds will the 
event be passed through to the Visitor it wraps, so you can filter out inner nodes and things like that, for example.

The usage of any of these is the same: `val result = spek.evaluate(visitor, candidate)` 

### SpekMatcher

A natural extension of the Visitor API is to use it to determine which nodes were actually hit during the process of 
evaluating the tree. For example, a boolean expression will fail-fast and ignore sub-trees of the Spek, but it would be
nice to _know_ which sub-trees were not evaluated. Matchers will provide you with this information.

Is essentially has two steps: the first step "explores" the Spek tree, recursively locating the children of each Spek 
until all nodes have been explored. Then, as the Spek is being evaluated, a Visitor can match the events received with
the nodes that it previously explored. After the evaluation is complete, you will be left with a `SpekMatcher` 
containing a list of all Nodes that were discovered, and related metadata about whether it was evaluated or not and its
result. 

Usage looks like the following. Basically, instead of calling `spek.evaluate(...)`, call the `spek.match()` extension
method. Instead of returning the result, you will receive back the `SpekMatcher`:

```kotlin
val matchResult = spek.match(input) {
    filter { it.children.isEmpty() } // only match leaf nodes
    onNodeFound { }                  // Receive an additional event whenever a node is discovered in the exploration phase
    onNodeHit   { }                  // Receive an additional event whenever a node is hit in the evaluation phase
}
val result = matchResult.result // the value returned from the Spek
val matches = matchResult.matches // a list of matched Speks and metadata
```