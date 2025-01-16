package com.tksimeji.wobject.reflect;

import com.google.gson.JsonObject;
import com.tksimeji.wobject.api.BlockComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public final class WobjectBlockComponent extends WobjectComponent<Block, Material, BlockComponent> {
    public WobjectBlockComponent(@NotNull Field field) {
        super(field);
    }

    @Override
    public @Nullable Material getType(@NotNull Object wobject) {
        Block value = getValue(wobject);
        return value != null ? value.getType() : null;
    }

    @Override
    public @NotNull List<Material> getTypes() {
        return List.of(annotation.value());
    }

    @Override
    public void setValue(@NotNull Object wobject, @Nullable Block value) {
        if (value != null && ! getTypes().contains(value.getType())) {
            throw new IllegalArgumentException();
        }

        super.setValue(wobject, value);
    }

    @Override
    public @NotNull Class<Block> getJavaClass() {
        return Block.class;
    }

    @Override
    public @NotNull Class<BlockComponent> getAnnotationClass() {
        return BlockComponent.class;
    }

    @Override
    public @NotNull ItemStack asItemStack(@NotNull UUID uuid, int texture) {
        return asItemStack(new ItemStack(getTypes().get(texture)), uuid);
    }

    @Override
    public @Nullable JsonObject asJsonObject(@NotNull Object wobject) {
        Block value = getValue(wobject);

        if (value == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", value.getWorld().getKey().asString());
        jsonObject.addProperty("x", value.getX());
        jsonObject.addProperty("y", value.getY());
        jsonObject.addProperty("z", value.getZ());
        return jsonObject;
    }
}
