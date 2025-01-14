package com.tksimeji.wobject.ui;

import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.reflect.WobjectComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ComponentUI extends ChestUI {
    private static final int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    private final @NotNull WobjectComponent component;
    private final @NotNull Block block;
    private final @NotNull WobjectBuilder<?> builder;

    private final int page;

    private final @NotNull Map<Integer, Material> types = new HashMap<>();

    public ComponentUI(@NotNull Player player, @NotNull WobjectComponent component, @NotNull Block block, @NotNull WobjectBuilder<?> builder) {
        this(player, component, block, builder, 0);
    }

    public ComponentUI(@NotNull Player player, @NotNull WobjectComponent component, @NotNull Block block, @NotNull WobjectBuilder<?> builder, int page) {
        super(player);
        this.component = component;
        this.block = block;
        this.builder = builder;
        this.page = page;

        int i = 0;

        for (Material type : component.getTypes().subList(page * slots.length, Math.min((page + 1) * slots.length, component.getTypes().size()))) {
            ItemStack itemStack = new ItemStack(type, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setHideTooltip(true);
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(slots[i], itemStack);
            types.put(slots[i ++], type);
        }

        ItemStack previousStack = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previousStack.getItemMeta();
        previousMeta.displayName(Component.text("Previous").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        previousStack.setItemMeta(previousMeta);
        inventory.setItem(getSize().asInt() - 9, previousStack);

        ItemStack nextStack = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextStack.getItemMeta();
        nextMeta.displayName(Component.text("Next").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        nextStack.setItemMeta(nextMeta);
        inventory.setItem(getSize().asInt() - 1, nextStack);
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("Component");
    }

    @Override
    public @NotNull Size getSize() {
        return Size.SIZE_54;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (event.getSlot() == slots.length) {
            new ComponentUI(player, component, block, builder, Math.max(page - 1, 0));
            return;
        }

        if (event.getSlot() == slots.length + 8) {
            new ComponentUI(player, component, block, builder, Math.min(page + 1, component.getTypes().size() / getSize().asInt() - 9));
            return;
        }

        Material type = types.get(event.getSlot());

        if (type == null) {
            return;
        }

        close();
        block.setType(type);
        player.getInventory().setItemInMainHand(null);
        builder.put(component, block);
    }
}
