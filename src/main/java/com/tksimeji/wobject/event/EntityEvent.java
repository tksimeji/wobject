package com.tksimeji.wobject.event;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class EntityEvent extends Event {
    private final @NotNull Entity entity;

    public EntityEvent(@NotNull Entity entity) {
        this.entity = entity;
    }

    public @NotNull Entity getEntity() {
        return entity;
    }
}
