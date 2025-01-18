package com.tksimeji.wobject.event;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityInteractedEvent extends InteractEvent {
    private final @NotNull Entity entity;

    public EntityInteractedEvent(@NotNull Entity entity, @NotNull Player player, @NotNull Action action, @Nullable ItemStack item, @Nullable Location location) {
        super(player, action, item, location);
        this.entity = entity;
    }

    @Override
    public @NotNull String getDescription() {
        return "Called when an entity component is interacted with.";
    }

    public @NotNull Entity getEntity() {
        return entity;
    }
}
