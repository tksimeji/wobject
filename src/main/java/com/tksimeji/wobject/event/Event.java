package com.tksimeji.wobject.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Event {
    public @NotNull String getName() {
        return getClass().getSimpleName();
    }

    public @Nullable String getDescription() {
        return null;
    }
}
