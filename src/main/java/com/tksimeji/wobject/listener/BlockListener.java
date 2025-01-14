package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.api.Handler;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectComponent;
import com.tksimeji.wobject.ui.TypeSelectorUI;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
            new TypeSelectorUI(event, component, builder);
            return;
        }

        builder.put(component, event.getBlock());
        player.getInventory().setItemInMainHand(null);
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();
        Object wobject = Wobject.get(block);

        if (wobject == null) {
            Optional.ofNullable(WobjectBuilder.get(block)).ifPresent(WobjectBuilder::kill);
            return;
        }

        WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());
        clazz.kill(wobject);
    }

    @EventHandler
    public void onBlockRedstone(@NotNull BlockRedstoneEvent event) {
        Object wobject = Wobject.get(event.getBlock());

        if (wobject == null) {
            return;
        }

        WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());
        WobjectComponent component = clazz.getComponent(wobject, event.getBlock());

        if (component == null) {
            return;
        }

        clazz.call(wobject, clazz.getRedstoneHandlers().stream().filter(handler -> {
            Handler.Redstone annotation = handler.getAnnotation(Handler.Redstone.class);
            List<String> components = Arrays.asList(annotation.component());
            return components.isEmpty() || components.contains(component.getName());
        }).collect(Collectors.toSet()), event, event.getBlock());
    }
}
