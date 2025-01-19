package com.tksimeji.wobject.reflect;

import com.google.gson.JsonObject;
import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.api.EntityComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class WobjectEntityComponent extends WobjectComponent<Entity, EntityType, EntityComponent> {
    WobjectEntityComponent(@NotNull WobjectClass<?> clazz, @NotNull Field field) {
        super(clazz, field);
    }

    @Override
    public @Nullable EntityType getType(@NotNull Object wobject) {
        Entity value = getValue(wobject);
        return value != null ? value.getType() : null;
    }

    @Override
    public @NotNull List<EntityType> getTypes() {
        return List.of(annotation.value());
    }

    @Override
    public void setValue(@NotNull Object wobject, @Nullable Entity value) {
        if (value != null && ! getTypes().contains(value.getType())) {
            throw new IllegalArgumentException();
        }

        super.setValue(wobject, value);
    }

    @Override
    public boolean isValidValue(@NotNull Object wobject) {
        Entity value = getValue(wobject);
        return value != null && ! value.isDead() && getTypes().contains(value.getType());
    }

    @Override
    public @NotNull Class<Entity> getJavaClass() {
        return Entity.class;
    }

    @Override
    public @NotNull Class<EntityComponent> getAnnotationClass() {
        return EntityComponent.class;
    }

    @Override
    public @NotNull ItemStack asItemStack(@NotNull UUID uuid, int texture) {
        EntityType entityType = getTypes().get(texture);
        Material itemType = Optional.ofNullable(Material.getMaterial(entityType.name().toUpperCase() + "_SPAWN_EGG")).orElse(Material.valueOf(entityType.name().toUpperCase()));

        ItemStack itemStack = asItemStack(new ItemStack(itemType), uuid);
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(new NamespacedKey(Wobject.plugin(), "entity"), PersistentDataType.STRING, entityType.name());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public @Nullable JsonObject asJsonObject(@NotNull Object wobject) {
        Entity value = getValue(wobject);

        if (value == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", value.getUniqueId().toString());
        return jsonObject;
    }
}
