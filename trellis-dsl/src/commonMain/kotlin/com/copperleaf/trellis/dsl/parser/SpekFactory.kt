package com.copperleaf.trellis.dsl.parser

import com.copperleaf.trellis.base.Spek

typealias SpekFactory = (List<Spek<*, *>>) -> Spek<*, *>
