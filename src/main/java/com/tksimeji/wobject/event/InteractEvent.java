package com.tksimeji.wobject.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InteractEvent extends PlayerEvent implements Cancellable {
    private final @NotNull Action action;
    private final @Nullable ItemStack item;
    private final @Nullable Location location;

    private boolean cancelled = true;

    public InteractEvent(@NotNull Player player, @NotNull Action action, @Nullable ItemStack item, @Nullable Location location) {
        super(player);

        this.action = action;
        this.item = item;
        this.location = location;
    }

    public @NotNull Action getAction() {
        return action;
    }

    public @Nullable ItemStack getItem() {
        return item;
    }

    public boolean hasItem() {
        return item != null;
    }

    public @Nullable Location getLocation() {
        return location;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancel(boolean cancel) {
        cancelled = cancel;
    }

    public boolean isLeftClick() {
        return action.isLeftClick();
    }

    public boolean isRightClick() {
        return action.isRightClick();
    }
}
