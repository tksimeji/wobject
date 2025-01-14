package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectComponent;
import com.tksimeji.wobject.ui.ComponentUI;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
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

        if (component == null) {
            return;
        }

        if (1 < component.getTypes().size()) {
            event.setCancelled(true);
            new ComponentUI(player, component, event.getBlock(), builder);
            return;
        }

        builder.put(component, event.getBlock());
        player.getInventory().setItemInMainHand(null);
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        for (WobjectClass<?> clazz : WobjectClass.all()) {
            for (Object wobject : clazz.getWobjects()) {
                if (clazz.getComponents().stream().anyMatch(component -> {
                    Block block = component.getValue(wobject);
                    return block != null && event.getBlock().getLocation().equals(block.getLocation());
                })) {
                    clazz.kill(wobject);
                }
            }
        }
    }
}
