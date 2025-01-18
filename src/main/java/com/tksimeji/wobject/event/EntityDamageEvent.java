package com.tksimeji.wobject.event;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntityDamageEvent extends EntityEvent implements Cancellable {
    private final @NotNull DamageSource source;
    private double damage;

    private boolean cancelled;

    public EntityDamageEvent(@NotNull Entity entity, @Nullable DamageSource source, double damage) {
        super(entity);

        this.source = source;
        this.damage = damage;
    }

    public @NotNull DamageSource getSource() {
        return source;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
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
