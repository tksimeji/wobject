package com.tksimeji.wobject.event;

import org.jetbrains.annotations.Nullable;

public class KillEvent extends Event {
    @Override
    public @Nullable String getDescription() {
        return "Called when the wobject is killed.";
    }
}
