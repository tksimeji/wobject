package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class BlockListener implements Listener {
    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

        if (! container.has(new NamespacedKey(Wobject.plugin(), "class"), PersistentDataType.STRING)) {
            return;
        }

        WobjectBuilder<?> builder = WobjectBuilder.get(UUID.fromString(container.get(new NamespacedKey(Wobject.plugin(), "uuid"), PersistentDataType.STRING)));

        if (builder == null) {
            return;
        }

        WobjectComponent component = builder.getWobjectClass().getComponent(container.get(new NamespacedKey(Wobject.plugin(), "component"), PersistentDataType.STRING));
        builder.put(component, event.getBlock());
        player.getInventory().setItemInMainHand(null);
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        for (WobjectClass<?> clazz : WobjectClass.all()) {
            for (Object wobject : clazz.getWobjects()) {
                for (WobjectComponent component : clazz.getComponents()) {
                    if (event.getBlock().getLocation().equals(component.getValue(wobject))) {
                        clazz.kill(wobject);
                    }
                }
            }
        }
    }
}
