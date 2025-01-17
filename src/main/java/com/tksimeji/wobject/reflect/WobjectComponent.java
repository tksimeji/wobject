package com.tksimeji.wobject.reflect;

import com.tksimeji.wobject.Wobject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.UUID;

public abstract class WobjectComponent<V, E extends Enum<?>, A extends Annotation> implements IWobjectComponent<V, E, A> {
    protected final @NotNull Field field;
    protected final @NotNull A annotation;

    public WobjectComponent(@NotNull Field field) {
        if (! field.isAnnotationPresent(getAnnotationClass())) {
            throw new IllegalArgumentException();
        }

        if (! getJavaClass().isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException();
        }

        this.field = field;
        annotation = field.getAnnotation(getAnnotationClass());
    }

    @Override
    public final String getName() {
        return field.getName();
    }

    @Override
    public @Nullable V getValue(@NotNull Object wobject) {
        try {
            field.setAccessible(true);
            return (V) field.get(wobject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(@NotNull Object wobject, @Nullable V value) {
        try {
            field.setAccessible(true);
            field.set(wobject, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Wobject.saveJson();
    }

    @Override
    public boolean hasValue(@NotNull Object wobject) {
        return getValue(wobject) != null;
    }

    @Override
    public @NotNull A getAnnotation() {
        return annotation;
    }

    @Override
    public final int getModifiers() {
        return field.getModifiers();
    }

    @Override
    public final @NotNull WobjectClass<?> getWobjectClass() {
        return WobjectClass.of(getDeclaringClass());
    }

    @Override
    public final Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public final boolean isSynthetic() {
        return field.isSynthetic();
    }

    @Override
    public final @NotNull Field asJavaField() {
        return field;
    }

    protected final @NotNull ItemStack asItemStack(@NotNull ItemStack itemStack, @NotNull UUID uuid) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(getName()).color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        itemMeta.setHideTooltip(true);
        itemMeta.addEnchant(Enchantment.INFINITY, 1, false);

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(new NamespacedKey(Wobject.plugin(), "builder"), PersistentDataType.STRING, uuid.toString());
        container.set(new NamespacedKey(Wobject.plugin(), "class"), PersistentDataType.STRING, getWobjectClass().getKey().asString());
        container.set(new NamespacedKey(Wobject.plugin(), "component"), PersistentDataType.STRING, getName());

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
