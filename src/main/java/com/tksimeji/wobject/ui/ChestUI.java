package com.tksimeji.wobject.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class ChestUI implements IChestUI {
    private static final Set<ChestUI> instances = new HashSet<>();

    public static @Nullable ChestUI getInstance(@Nullable Player player) {
        return instances.stream().filter(instance -> instance.getPlayer() == player).findFirst().orElse(null);
    }

    public static @Nullable ChestUI getInstance(@Nullable Inventory inventory) {
        return instances.stream().filter(instance -> instance.getInventory() == inventory).findFirst().orElse(null);
    }

    protected final @NotNull Player player;
    protected final @NotNull Inventory inventory;

    public ChestUI(@NotNull Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(null, getSize().asInt(), getTitle());

        instances.add(this);
        new ArrayList<>(instances).stream()
                .filter(instance -> instance != this && instance.getPlayer() == player)
                .forEach(ChestUI::close);

        player.openInventory(inventory);
    }

    @Override
    public final @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public final @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public final void close() {
        onClose();
        instances.remove(this);

        if (! player.getOpenInventory().getTopInventory().isEmpty() &&
                instances.stream().noneMatch(instance -> instance != this && instance.getPlayer() == player)) {
            player.closeInventory();
        }

        player.updateInventory();
    }
}
