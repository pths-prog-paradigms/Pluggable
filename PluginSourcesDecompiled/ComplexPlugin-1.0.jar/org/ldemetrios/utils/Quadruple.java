package org.ldemetrios.utils;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Quadruple {
    private final int w;
    private final int x;
    private final int y;
    private final int z;

    public Quadruple(int w, int x, int y, int z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final int sum() {
        return this.w + this.x + this.y + this.z;
    }
}
