package com.copperleaf.trellis.dsl.parser

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.trellis.base.Spek

public typealias SpekFactory = (List<Spek<*, *>>) -> Spek<*, *>

public typealias SpekNode = ValueNode<Spek<Any?, Any?>>
public typealias SpekListNode = ValueNode<List<Spek<Any?, Any?>>>
