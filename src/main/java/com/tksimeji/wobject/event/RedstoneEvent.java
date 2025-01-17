package com.tksimeji.wobject.event;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public final class RedstoneEvent extends Event implements Cancellable {
    private final @NotNull Block block;

    private final int from;
    private int to;

    private boolean cancelled;

    public RedstoneEvent(@NotNull Block block, int from, int to) {
        this.block = block;
        this.from = from;
        this.to = to;
    }

    @Override
    public @NotNull String getDescription() {
        return "Called when the regstone signal supplied to a block component changes.";
    }

    public @NotNull Block getBlock() {
        return block;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancel(boolean cancel) {
        cancelled = cancel;
    }

    public boolean isIncrease() {
        return from < to;
    }

    public boolean isDecrease() {
        return from > to;
    }
}
