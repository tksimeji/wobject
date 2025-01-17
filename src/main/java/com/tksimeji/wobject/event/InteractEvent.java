package com.tksimeji.wobject.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InteractEvent extends Event implements Cancellable {
    private final @NotNull Object component;
    private final @NotNull Player player;
    private final @NotNull Action action;
    private final @Nullable ItemStack hand;
    private final @Nullable Location location;

    private boolean cancelled;

    public InteractEvent(@NotNull Block component, @NotNull Player player, @NotNull Action action, @Nullable ItemStack hand, @Nullable Location location) {
        this((Object) component, player, action, hand, location);
    }

    public InteractEvent(@NotNull Entity component, @NotNull Player player, @NotNull Action action, @Nullable ItemStack hand, @Nullable Location location) {
        this((Object) component, player, action, hand, location);
    }

    private InteractEvent(@NotNull Object component, @NotNull Player player, @NotNull Action action, @Nullable ItemStack hand, @Nullable Location location) {
        this.component = component;
        this.player = player;
        this.action = action;
        this.hand = hand;
        this.location = location;
    }

    @Override
    public @NotNull String getDescription() {
        return "Called when a component is interacted with by a player.";
    }

    public @Nullable Block getBlock() {
        return component instanceof Block block ? block : null;
    }

    public @Nullable Entity getEntity() {
        return component instanceof Entity entity ? entity : null;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Action getAction() {
        return action;
    }

    public @Nullable ItemStack getHand() {
        return hand;
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

    public boolean isBlock() {
        return getBlock() != null;
    }

    public boolean isEntity() {
        return getEntity() != null;
    }

    public boolean isLeftClick() {
        return action == Action.LEFT_CLICK;
    }

    public boolean isRightClick() {
        return action == Action.RIGHT_CLICK;
    }

    public enum Action {
        LEFT_CLICK,
        RIGHT_CLICK
    }
}
