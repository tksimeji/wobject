package com.tksimeji.wobject.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockBreakEvent extends BlockEvent implements Cancellable {
    private final @Nullable Player player;
    private boolean drop = true;

    private boolean cancelled;

    public BlockBreakEvent(@NotNull Block block, @Nullable Player player) {
        super(block);

        this.player = player;
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancel(boolean cancel) {
        cancelled = cancel;
    }
}
