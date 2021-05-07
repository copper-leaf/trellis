package com.copperleaf.trellis.dsl.parser

import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.trellis.base.Spek

typealias SpekFactory = (List<Spek<*, *>>) -> Spek<*, *>

typealias SpekNode = ValueNode<Spek<Any?, Any?>>
typealias SpekListNode = ValueNode<List<Spek<Any?, Any?>>>
