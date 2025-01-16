package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.api.Handler;
import com.tksimeji.wobject.reflect.WobjectBlockComponent;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectEntityComponent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Object wobject = Wobject.get(block);

        if (wobject == null) {
            return;
        }

        WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());
        WobjectBlockComponent component = clazz.getBlockComponent(wobject, block);

        if (component == null) {
            return;
        }

        clazz.call(wobject, clazz.getInteractHandlers().stream().filter(handler -> {
            Handler.Interact annotation = handler.getAnnotation(Handler.Interact.class);
            List<String> components = Arrays.asList(annotation.component());
            return components.isEmpty() || components.contains(component.getName());
        }).collect(Collectors.toSet()), event, event.getPlayer(), block);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract2(@NotNull PlayerInteractEvent event) {
        Location point = event.getInteractionPoint();
        ItemStack itemStack = event.getItem();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || point == null || itemStack == null) {
            return;
        }

        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

        if (! container.has(new NamespacedKey(Wobject.plugin(), "class")) || itemStack.getType().isBlock()) {
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

        event.setCancelled(true);
        event.getPlayer().getInventory().setItemInMainHand(null);

        EntityType entityType = EntityType.valueOf(container.get(new NamespacedKey(Wobject.plugin(), "entity"), PersistentDataType.STRING));
        Entity entity = point.getWorld().spawnEntity(point, entityType);
        entity.setGravity(false);
        entity.setPersistent(true);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setAI(false);
            livingEntity.setCollidable(false);
        }

        builder.put(component, entity);
    }
}
