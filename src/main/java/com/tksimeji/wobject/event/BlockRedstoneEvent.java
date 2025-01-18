package com.tksimeji.wobject.event;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public final class BlockRedstoneEvent extends BlockEvent {
    private final int from;
    private int to;

    private boolean cancelled;

    public BlockRedstoneEvent(@NotNull Block block, int from, int to) {
        super(block);

        this.from = from;
        this.to = to;
    }

    @Override
    public @NotNull String getDescription() {
        return "Called when the regstone signal supplied to a block component changes.";
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

    public boolean isIncrease() {
        return from < to;
    }

    public boolean isDecrease() {
        return from > to;
    }
}
