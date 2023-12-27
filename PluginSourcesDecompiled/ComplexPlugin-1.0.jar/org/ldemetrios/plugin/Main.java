package org.ldemetrios.plugin;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.ldemetrios.utils.Quadruple;
import org.ldemetrios.utils.QuadrupleKt;

@Metadata(
        mv = {1, 9, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0004"},
        d2 = {"Lorg/ldemetrios/plugin/Main;", "", "()V", "Companion", "ComplexPlugin"}
)
public final class Main {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);

    @JvmStatic
    public static final short function(short x) {
        return Companion.function(x);
    }

    @Metadata(
            mv = {1, 9, 0},
            k = 1,
            xi = 48,
            d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\n\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0007¨\u0006\u0006"},
            d2 = {"Lorg/ldemetrios/plugin/Main$Companion;", "", "()V", "function", "", "x", "ComplexPlugin"}
    )
    public static final class Companion {
        private Companion() {
        }

        @JvmStatic
        public final short function(short x) {
            return (short)(new Quadruple(QuadrupleKt.toHexAndHash((Number)x), QuadrupleKt.toHexAndHash((Number)x * 5), QuadrupleKt.toHexAndHash((Number)x + 7), QuadrupleKt.toHexAndHash((Number)x / 11))).sum();
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
