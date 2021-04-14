package com.copperleaf.trellis.dsl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.impl.and
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@Suppress("UNCHECKED_CAST")
class NullableExpressionsTest {

    @ParameterizedTest
    @MethodSource("testArgs")
    fun testBuilderFormat(user: User?, result: Boolean) {
        val spek = UserExistsSpek() and IsLoggedInSpek() and IsVerifiedSpek()

        expectThat(spek.evaluate(EmptyVisitor, user)).isEqualTo(result)
    }

    @ParameterizedTest
    @MethodSource("testArgs")
    fun testExpressionFormat(user: User?, result: Boolean) {

        expectThat(
            TrellisDsl.evaluate<User?, Boolean>(
                context,
                "userExists and isLoggedIn and isVerified",
                user
            )
        ).isEqualTo(result)
    }

    companion object {

        @JvmStatic
        fun testArgs(): List<Arguments> = listOf(
            Arguments.of(User(true, true), true),
            Arguments.of(User(true, false), false),
            Arguments.of(User(false, true), false),
            Arguments.of(User(false, false), false),
            Arguments.of(null, false)
        )
    }

    val context = DiscountContext {
        register { _, _ -> UserExistsSpek() }
        register { _, _ -> IsLoggedInSpek() }
        register { _, _ -> IsVerifiedSpek() }
    }

    class DiscountContext(
        initializer: (SpekExpressionContext.() -> Unit)? = null
    ) : SpekExpressionContext(initializer)

    data class User(
        val loggedIn: Boolean,
        val verified: Boolean
    )

    class UserExistsSpek : Spek<User?, Boolean> {
        override fun evaluate(visitor: SpekVisitor, candidate: User?) = visiting(visitor) {
            candidate != null
        }
    }

    class IsLoggedInSpek : Spek<User?, Boolean> {
        override fun evaluate(visitor: SpekVisitor, candidate: User?) = visiting(visitor) {
            candidate?.loggedIn ?: false
        }
    }

    class IsVerifiedSpek : Spek<User?, Boolean> {
        override fun evaluate(visitor: SpekVisitor, candidate: User?) = visiting(visitor) {
            candidate?.verified ?: false
        }
    }
}
