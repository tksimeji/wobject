package com.tksimeji.wobject.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public interface IChestUI {
    @NotNull Component getTitle();

    @NotNull Size getSize();

    @NotNull Player getPlayer();

    @NotNull Inventory getInventory();

    default void onClick(@NotNull InventoryClickEvent event) {}

    default void onClose() {}

    void close();
}
