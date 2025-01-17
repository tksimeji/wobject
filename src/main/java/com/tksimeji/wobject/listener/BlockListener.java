package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.event.RedstoneEvent;
import com.tksimeji.wobject.reflect.WobjectBlockComponent;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.ui.TypeSelectorUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public final class BlockListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

        if (! container.has(new NamespacedKey(Wobject.plugin(), "class"), PersistentDataType.STRING)) {
            return;
        }

        WobjectBuilder<?> builder = WobjectBuilder.get(UUID.fromString(container.get(new NamespacedKey(Wobject.plugin(), "builder"), PersistentDataType.STRING)));

        if (builder == null) {
            return;
        }

        WobjectBlockComponent component = builder.getWobjectClass().getBlockComponent(container.get(new NamespacedKey(Wobject.plugin(), "component"), PersistentDataType.STRING));

        if (component == null) {
            return;
        }

        if (1 < component.getTypes().size()) {
            event.setCancelled(true);
            new TypeSelectorUI(event, component, builder);
            return;
        }

        builder.put(component, event.getBlock());
        player.getInventory().setItemInMainHand(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();
        Object wobject = Wobject.get(block);

        if (wobject == null) {
            Optional.ofNullable(WobjectBuilder.get(block)).ifPresent(WobjectBuilder::kill);
            return;
        }

        Player player = event.getPlayer();

        if (! player.hasPermission("wobject.break")) {
            player.sendMessage(Component.text("You do not have permission to do this.").color(NamedTextColor.RED));
            event.setCancelled(true);
            return;
        }

        WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());
        clazz.kill(wobject);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRedstone(@NotNull BlockRedstoneEvent event) {
        Object wobject = Wobject.get(event.getBlock());

        if (wobject == null) {
            return;
        }

        WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());
        WobjectBlockComponent component = clazz.getBlockComponent(wobject, event.getBlock());

        if (component == null) {
            return;
        }

        clazz.call(wobject, new RedstoneEvent(event.getBlock(), event.getOldCurrent(), event.getNewCurrent()));
    }
}
