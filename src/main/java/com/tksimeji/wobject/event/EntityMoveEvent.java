package com.tksimeji.wobject.event;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public final class EntityMoveEvent extends EntityEvent implements Cancellable {
    private @NotNull Location from;
    private @NotNull Location to;

    private boolean cancelled;

    public EntityMoveEvent(@NotNull Entity entity, @NotNull Location from, @NotNull Location to) {
        super(entity);

        this.from = from;
        this.to = to;
    }

    public @NotNull Location getFrom() {
        return from;
    }

    public void setFrom(@NotNull Location from) {
        this.from = from;
    }

    public @NotNull Location getTo() {
        return to;
    }

    public void setTo(@NotNull Location to) {
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
}
