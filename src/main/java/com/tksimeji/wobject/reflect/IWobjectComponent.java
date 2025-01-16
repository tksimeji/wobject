package com.tksimeji.wobject.reflect;

import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.List;
import java.util.UUID;

public interface IWobjectComponent<V, E extends Enum<?>, A extends Annotation> extends Member {
    @Nullable V getValue(@NotNull Object wobject);

    void setValue(@NotNull Object wobject, @Nullable V value);

    @Nullable E getType(@NotNull Object wobject);

    @NotNull List<E> getTypes();

    @NotNull A getAnnotation();

    @NotNull Class<V> getJavaClass();

    @NotNull Class<A> getAnnotationClass();

    @NotNull WobjectClass<?> getWobjectClass();

    @NotNull Field asJavaField();

    default @NotNull ItemStack asItemStack(@NotNull UUID uuid) {
        return asItemStack(uuid, 0);
    }

    @NotNull ItemStack asItemStack(@NotNull UUID uuid, int texture);

    @Nullable JsonObject asJsonObject(@NotNull Object wobject);
}
