package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.reflect.WobjectEntityComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class EntityListener implements Listener {
    @EventHandler
    public void onHangingPlace(@NotNull HangingPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemStack();

        if (player == null || itemStack == null) {
            return;
        }

        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

        if (! container.has(new NamespacedKey(Wobject.plugin(), "class"))) {
            return;
        }

        WobjectBuilder<?> builder = WobjectBuilder.get(UUID.fromString(container.get(new NamespacedKey(Wobject.plugin(), "builder"), PersistentDataType.STRING)));

        if (builder == null) {
            return;
        }

        WobjectEntityComponent component = builder.getWobjectClass().getEntityComponent(container.get(new NamespacedKey(Wobject.plugin(), "component"), PersistentDataType.STRING));

        if (component == null) {
            return;
        }

        player.getInventory().setItemInMainHand(null);
        builder.put(component, event.getEntity());
    }
}
