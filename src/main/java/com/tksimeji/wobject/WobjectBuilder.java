package com.tksimeji.wobject;

import com.tksimeji.wobject.reflect.IWobjectComponent;
import com.tksimeji.wobject.reflect.WobjectBlockComponent;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectEntityComponent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class WobjectBuilder<T> extends HashMap<IWobjectComponent<?, ?, ?>, Object> {
    private static final @NotNull Set<WobjectBuilder<?>> instances = new HashSet<>();

    public static <T> @NotNull WobjectBuilder<T> create(@NotNull WobjectClass<T> clazz) {
        return new WobjectBuilder<>(clazz);
    }

    public static @Nullable WobjectBuilder<?> get(@Nullable UUID uuid) {
        return instances.stream().filter(instance -> instance.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public static @Nullable WobjectBuilder<?> get(@Nullable Block block) {
        if (block == null) {
            return null;
        }

        return instances.stream().filter(instance -> instance.blocks().stream().anyMatch(b -> b.getLocation().equals(block.getLocation()))).findFirst().orElse(null);
    }

    public static @Nullable WobjectBuilder<?> get(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }

        return instances.stream().filter(instance -> instance.entities().stream().anyMatch(e -> e.getUniqueId().equals(entity.getUniqueId()))).findFirst().orElse(null);
    }

    public static @NotNull Set<WobjectBuilder<?>> all() {
        return new HashSet<>(instances);
    }

    private final @NotNull UUID uuid = UUID.randomUUID();
    private final @NotNull WobjectClass<T> clazz;

    private WobjectBuilder(@NotNull WobjectClass<T> clazz) {
        this.clazz = clazz;
        instances.add(this);
    }

    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    public @NotNull WobjectClass<T> getWobjectClass() {
        return clazz;
    }

    public @NotNull Set<Block> blocks() {
        return values().stream()
                .filter(value -> value instanceof Block)
                .map(value -> (Block) value)
                .collect(Collectors.toSet());
    }

    public @NotNull Set<Entity> entities() {
        return values().stream()
                .filter(value -> value instanceof Entity)
                .map(value -> (Entity) value)
                .collect(Collectors.toSet());
    }

    @Override
    public @Nullable Object put(@NotNull IWobjectComponent<?, ?, ?> key, @NotNull Object value) {
        if (! clazz.getComponents().contains(key)) {
            throw new IllegalArgumentException();
        }

        Object result;

        if (value instanceof Block block) {
            result = put(key, block);
        } else if (value instanceof Entity entity) {
            result = put(key, entity);
        } else {
            throw new UnsupportedOperationException();
        }

        if (clazz.getComponents().size() <= size()) {
            T wobject = clazz.newInstance(uuid);
            forEach((k, v) -> {
                if ((k instanceof WobjectBlockComponent blockComponent) && (v instanceof Block block)) {
                    blockComponent.setValue(wobject, block);
                } else if ((k instanceof WobjectEntityComponent entityComponent) && (v instanceof Entity entity)) {
                    entityComponent.setValue(wobject, entity);
                }
            });
            kill();
        }

        return result;
    }

    private @Nullable Block put(@NotNull IWobjectComponent<?, ?, ?> key, @NotNull Block value) {
        if (! key.getTypes().contains(value.getType())) {
            throw new IllegalArgumentException();
        }

        return (Block) super.put(key, value);
    }

    private @Nullable Entity put(@NotNull IWobjectComponent<?, ?, ?> key, @NotNull Entity value) {
        if (! key.getTypes().contains(value.getType())) {
            throw new IllegalArgumentException();
        }

        return (Entity) super.put(key, value);
    }

    public void kill() {
        instances.removeIf(instance -> instance == this);
    }
}
