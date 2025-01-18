package com.tksimeji.wobject.event;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public abstract class BlockEvent extends Event {
    private final @NotNull Block block;

    public BlockEvent(@NotNull Block block) {
        this.block = block;
    }

    public @NotNull Block getBlock() {
        return block;
    }
}
