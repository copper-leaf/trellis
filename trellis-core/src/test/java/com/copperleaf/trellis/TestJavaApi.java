package com.copperleaf.trellis;

import com.copperleaf.trellis.api.EmptyVisitor;
import com.copperleaf.trellis.api.JavaKt;
import com.copperleaf.trellis.api.Spek;
import com.copperleaf.trellis.api.SpekVisitor;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        Boolean aBoolean = JavaKt.evaluateSync(spek, EmptyVisitor.INSTANCE, input);
        assertEquals(false, aBoolean);
    }

    @Test
    public void testJavaEqualSpekPassSync() {
        String input = "qwerty";
        String expected = "qwerty";
        Spek<String, Boolean> spek = new JavaEqualSpek(expected);

        JavaKt.evaluateAsync(spek, EmptyVisitor.INSTANCE, input, new Function1<Boolean, Unit>() {
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

        JavaKt.evaluateAsync(spek, EmptyVisitor.INSTANCE, input, new Function1<Boolean, Unit>() {
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

        JavaKt.evaluateAsync(spek, EmptyVisitor.INSTANCE, input, new Function1<Boolean, Unit>() {
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

        @Override
        public Object evaluate(@Nullable SpekVisitor visitor, String candidate, @NotNull Continuation<? super Boolean> continuation) {
            try {
                Thread.sleep(5000);
            }
            catch (Exception e) {

            }
            coroutineIsCompleted = true;
            return input.equals(candidate);
        }

        @NotNull
        @Override
        public List<Spek<?, ?>> getChildren() {
            return new ArrayList<Spek<?, ?>>();
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
