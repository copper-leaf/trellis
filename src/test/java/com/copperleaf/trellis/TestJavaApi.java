package com.copperleaf.trellis;

import com.copperleaf.trellis.api.Spek;
import com.copperleaf.trellis.api.Spek;
import kotlin.coroutines.experimental.Continuation;
import kotlin.coroutines.experimental.CoroutineContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestJavaApi {

    @Test
    public void testJavaEqualSpekFail() {
        String input = "asdf";
        String expected = "qwerty";
        Spek<String, Boolean> spek = new JavaEqualSpek(expected);

        spek.evaluate(input, new Continuation<Boolean>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return null;
            }

            @Override
            public void resume(Boolean aBoolean) {
                assertEquals(false, aBoolean);
            }

            @Override
            public void resumeWithException(Throwable throwable) {
            }
        });
    }

    @Test
    public void testJavaEqualSpekPass() {
        String input = "qwerty";
        String expected = "qwerty";
        Spek<String, Boolean> spek = new JavaEqualSpek(expected);

        spek.evaluate(input, new Continuation<Boolean>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return null;
            }

            @Override
            public void resume(Boolean aBoolean) {
                assertEquals(true, aBoolean);
            }

            @Override
            public void resumeWithException(Throwable throwable) {
            }
        });
    }


    public static class JavaEqualSpek implements Spek<String, Boolean> {

        private final String input;

        public JavaEqualSpek(String input) {
            this.input = input;
        }

        @Nullable
        @Override
        public Object evaluate(String candidate, @NotNull Continuation<? super Boolean> continuation) {
            continuation.resume(input.equals(candidate));
            return null;
        }
    }
}
