package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.ui.ChestUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        ChestUI ui = ChestUI.getInstance(event.getClickedInventory());

        if (ui == null) {
            return;
        }

        ui.onClick(event);
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (! (event.getPlayer() instanceof Player player)) {
            return;
        }

        Optional.ofNullable(ChestUI.getInstance(player)).ifPresent(ChestUI::close);
    }
}
