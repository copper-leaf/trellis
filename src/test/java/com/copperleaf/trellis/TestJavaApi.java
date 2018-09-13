package com.copperleaf.trellis;

import com.copperleaf.trellis.api.JavaKt;
import com.copperleaf.trellis.api.Spek;
import kotlin.Unit;
import kotlin.coroutines.experimental.Continuation;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.awaitility.Awaitility.*;

public class TestJavaApi {

    private boolean coroutineIsCompleted = false;

    @BeforeEach
    void setUp() {
        coroutineIsCompleted = false;
    }

    @Test
    public void testJavaEqualSpekFailSync() {
        String input = "asdf";
        String expected = "qwerty";
        Spek<String, Boolean> spek = new JavaEqualSpek(expected);

        Boolean aBoolean = JavaKt.evaluateSync(spek, input);
        assertEquals(false, aBoolean);
    }

    @Test
    public void testJavaEqualSpekPassSync() {
        String input = "qwerty";
        String expected = "qwerty";
        Spek<String, Boolean> spek = new JavaEqualSpek(expected);

        JavaKt.evaluateAsync(spek, input, new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean aBoolean) {
                System.out.println("testJavaEqualSpekPassSync: " + aBoolean);
                assertEquals(true, aBoolean);
                return null;
            }
        });
    }

    @Test
    public void testJavaEqualSpekFailAsync() {
        String input = "asdf";
        String expected = "qwerty";
        Spek<String, Boolean> spek = new JavaEqualSpek(expected);

        JavaKt.evaluateAsync(spek, input, new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean aBoolean) {
                assertEquals(false, aBoolean);
                return null;
            }
        });
        await().until(coroutineIsCompleted());
    }

    @Test
    public void testJavaEqualSpekPassAsync() {
        String input = "qwerty";
        String expected = "qwerty";
        Spek<String, Boolean> spek = new JavaEqualSpek(expected);

        JavaKt.evaluateAsync(spek, input, new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean aBoolean) {
                assertEquals(true, aBoolean);
                return null;
            }
        });
        await().until(coroutineIsCompleted());
    }

    public class JavaEqualSpek implements Spek<String, Boolean> {

        private final String input;

        public JavaEqualSpek(String input) {
            this.input = input;
        }

        @Nullable
        @Override
        public Object evaluate(String candidate, @NotNull Continuation<? super Boolean> continuation) {
            try {
                Thread.sleep(5000);
            }
            catch (Exception e) {

            }
            coroutineIsCompleted = true;
            return input.equals(candidate);
        }
    }

    private Callable<Boolean> coroutineIsCompleted() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return coroutineIsCompleted;
            }
        };
    }
}
