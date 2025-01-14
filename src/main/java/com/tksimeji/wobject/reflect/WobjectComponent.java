package com.tksimeji.wobject.reflect;

import com.google.gson.JsonObject;
import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.api.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.List;
import java.util.UUID;

public final class WobjectComponent implements Member {
    private final @NotNull Field field;
    private final @NotNull Component annotation;

    WobjectComponent(@NotNull Field field) {
        if (! field.isAnnotationPresent(Component.class)) {
            throw new IllegalArgumentException();
        }

        if (! Block.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException();
        }

        this.field = field;
        annotation = field.getAnnotation(Component.class);

        if (getTypes().isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public @NotNull String getName() {
        return field.getName();
    }

    public @NotNull List<Material> getTypes() {
        return List.of(annotation.type());
    }

    public @Nullable Block getValue(@NotNull Object wobject) {
        try {
            field.setAccessible(true);
            return (Block) field.get(wobject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(@NotNull Object wobject, @Nullable Block value) {
        if (value != null && ! getTypes().contains(value.getType())) {
            throw new IllegalArgumentException();
        }

        try {
            field.setAccessible(true);
            field.set(wobject, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Wobject.setJson();
    }

    @Override
    public int getModifiers() {
        return field.getModifiers();
    }

    public @NotNull WobjectClass<?> getWobjectClass() {
        return WobjectClass.of(getDeclaringClass());
    }

    @Override
    public @NotNull Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public boolean isSynthetic() {
        return field.isSynthetic();
    }

    public @NotNull ItemStack asItemStack(@NotNull UUID uuid) {
        return asItemStack(uuid, 0);
    }

    public @NotNull ItemStack asItemStack(@NotNull UUID uuid, int texture) {
        if (texture < 0 || getTypes().size() <= texture || ! getTypes().contains(getTypes().get(texture))) {
            throw new IllegalArgumentException();
        }

        ItemStack itemStack = new ItemStack(getTypes().get(texture), 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(net.kyori.adventure.text.Component.text(getName()).color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        itemMeta.addEnchant(Enchantment.INFINITY, 1, false);

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(new NamespacedKey(Wobject.plugin(), "class"), PersistentDataType.STRING, getWobjectClass().getName());
        container.set(new NamespacedKey(Wobject.plugin(), "component"), PersistentDataType.STRING, getName());
        container.set(new NamespacedKey(Wobject.plugin(), "uuid"), PersistentDataType.STRING, uuid.toString());

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public @Nullable JsonObject asJsonObject(@NotNull Object wobject) {
        Block block = getValue(wobject);

        if (block == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", block.getWorld().getKey().asString());
        jsonObject.addProperty("x", block.getX());
        jsonObject.addProperty("y", block.getY());
        jsonObject.addProperty("z", block.getZ());
        return jsonObject;
    }
}
