package com.tksimeji.wobject.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerEvent extends Event {
    private final @NotNull Player player;

    public PlayerEvent(@NotNull Player player) {
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return player;
    }
}
