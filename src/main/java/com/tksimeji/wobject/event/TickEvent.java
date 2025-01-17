package com.tksimeji.wobject.event;

import org.jetbrains.annotations.NotNull;

public final class TickEvent extends Event {
    private int number;

    public TickEvent(int number) {
        this.number = number;
    }

    @Override
    public @NotNull String getDescription() {
        return "Called every server tick.";
    }

    public int getTickNumber() {
        return number;
    }
}
