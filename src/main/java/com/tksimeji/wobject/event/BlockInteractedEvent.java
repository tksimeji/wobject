package com.tksimeji.wobject.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockInteractedEvent extends InteractEvent {
    private final @NotNull Block block;

    public BlockInteractedEvent(@NotNull Block block, @NotNull Player player, @NotNull Action action, @Nullable ItemStack item, @Nullable Location location) {
        super(player, action, item, location);
        this.block = block;
    }

    public @NotNull Block getBlock() {
        return block;
    }
}
