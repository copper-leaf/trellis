package com.copperleaf.trellis.dsl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.dsl.visitor.TrellisDslVisitor.Companion.create
import com.copperleaf.trellis.dsl.visitor.typeSafe
import com.copperleaf.trellis.impl.MinLengthSpek
import com.copperleaf.trellis.impl.ValueSpek
import com.copperleaf.trellis.impl.and
import com.copperleaf.trellis.impl.andNot
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expect
import strikt.assertions.isEqualTo

@Suppress("UNCHECKED_CAST")
class TrellisPasswordRequirementsTest {

    @ParameterizedTest
    @MethodSource("testArgs")
    fun testBuilderFormat(candidate: String, currentUsername: String, isValid: Boolean) {
        val spek = StringContainsSpek("\\w")
            .and(StringContainsSpek("\\d"))
            .and(MinLengthSpek(8))
            .andNot(CurrentUsernameSpek(currentUsername))

        expect {
            that(spek.evaluate(EmptyVisitor, candidate)).isEqualTo(isValid)
        }
    }

    @ParameterizedTest
    @MethodSource("testArgs")
    fun testExpressionFormat(candidate: String, currentUsername: String, isValid: Boolean) {
        context.currentUsername = currentUsername
        val spek: Spek<String, Boolean> = create(
            context,
            """
            |contains('\w') and contains('\d') and minLength(8) and !username()
            """.trimMargin()
        )

        expect {
            that(spek.evaluate(EmptyVisitor, candidate)).isEqualTo(isValid)
        }
    }

    companion object {

        @JvmStatic
        fun testArgs(): List<Arguments> = listOf(
            Arguments.of("asdf", "user1", false),
            Arguments.of("asdf1", "user1", false),
            Arguments.of("asdfasdf1", "user1", true)
        )
    }

    val context = CurrentAccountContext {
        register("contains") { cxt, args ->
            StringContainsSpek(args[0].typeSafe<Any, Any, String, String>(cxt))
        }
        register("minLength") { cxt, args ->
            MinLengthSpek(args[0].typeSafe<Any, Any, String, Int>(cxt))
        }
        register("username") { cxt, _ ->
            CurrentUsernameSpek(ValueSpek((cxt as CurrentAccountContext).currentUsername))
        }
    }

    class CurrentAccountContext(initializer: (SpekExpressionContext.() -> Unit)? = null) :
        SpekExpressionContext(initializer) {

        var currentUsername: String = ""

    }

    class StringContainsSpek(private val expectedString: Spek<String, String>) : Spek<String, Boolean> {
        constructor(expectedString: String) : this(ValueSpek(expectedString))

        override val children = listOf(expectedString)

        override suspend fun evaluate(visitor: SpekVisitor, candidate: String): Boolean {
            return visiting(visitor) {
                val stringValue = expectedString.evaluate(visitor, candidate)
                candidate.contains(stringValue.toRegex())
            }
        }
    }

    class CurrentUsernameSpek(private val currentUsername: Spek<String, String>) : Spek<String, Boolean> {
        constructor(expectedString: String) : this(ValueSpek(expectedString))

        override val children = listOf(currentUsername)

        override suspend fun evaluate(visitor: SpekVisitor, candidate: String): Boolean {
            return visiting(visitor) {
                candidate == currentUsername.evaluate(visitor, candidate)
            }
        }
    }

}
