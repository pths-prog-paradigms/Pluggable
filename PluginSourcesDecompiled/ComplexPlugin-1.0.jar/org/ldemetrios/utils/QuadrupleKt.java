package org.ldemetrios.utils;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.CharsKt;
import org.jetbrains.annotations.NotNull;

public final class QuadrupleKt {
    public static final int toHexAndHash(@NotNull Number $this$toHexAndHash) {
        Intrinsics.checkNotNullParameter($this$toHexAndHash, "<this>");
        String var10000 = Integer.toString($this$toHexAndHash.intValue(), CharsKt.checkRadix(16));
        Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
        return var10000.hashCode();
    }
}
