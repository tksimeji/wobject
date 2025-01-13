package com.tksimeji.wobject;

import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectComponent;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class WobjectBuilder<T> extends HashMap<WobjectComponent, Block> {
    private static final @NotNull Set<WobjectBuilder<?>> instances = new HashSet<>();

    public static <T> @NotNull WobjectBuilder<T> create(@NotNull WobjectClass<T> clazz) {
        return new WobjectBuilder<>(clazz);
    }

    public static @Nullable WobjectBuilder<?> get(@Nullable UUID uuid) {
        return instances.stream().filter(instance -> instance.getUniqueId().equals(uuid)).findFirst().orElse(null);
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

    @Override
    public @Nullable Block put(@NotNull WobjectComponent key, @NotNull Block value) {
        if (! clazz.getComponents().contains(key)) {
            throw new IllegalArgumentException();
        }

        if (key.getType() != value.getType()) {
            throw new IllegalArgumentException();
        }

        Block result = super.put(key, value);

        if (clazz.getComponents().size() <= size()) {
            T wobject = clazz.newInstance(uuid);
            forEach((component, block) -> component.setValue(wobject, block));
            instances.remove(this);
        }

        return result;
    }
}
