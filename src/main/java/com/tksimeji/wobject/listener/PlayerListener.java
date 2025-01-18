package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.event.BlockInteractedEvent;
import com.tksimeji.wobject.event.EntityInteractedEvent;
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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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

        event.setCancelled(clazz.call(wobject, new BlockInteractedEvent(block, event.getPlayer(), event.getAction(), event.getItem(), event.getInteractionPoint())).isCancelled());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract2(@NotNull PlayerInteractEvent event) {
        Location point = event.getInteractionPoint();
        ItemStack item = event.getItem();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || point == null || item == null) {
            return;
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        if (! container.has(new NamespacedKey(Wobject.plugin(), "class")) || item.getType().isBlock()) {
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

        EntityType entityType = EntityType.valueOf(container.get(new NamespacedKey(Wobject.plugin(), "entity"), PersistentDataType.STRING));

        if (! entityType.isAlive()) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().getInventory().setItemInMainHand(null);

        Entity entity = point.getWorld().spawnEntity(point, entityType);
        entity.setGravity(component.getAnnotation().gravity());
        entity.setSilent(component.getAnnotation().silent());
        entity.setPersistent(true);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setAI(component.getAnnotation().ai());
            livingEntity.setCollidable(component.getAnnotation().collidable());
        }

        builder.put(component, entity);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractAtEntity(@NotNull PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        Object wobject = Wobject.get(entity);

        if (wobject == null) {
            return;
        }

        WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());
        WobjectEntityComponent component = clazz.getEntityComponent(wobject, entity);

        if (component == null) {
            return;
        }

        event.setCancelled(clazz.call(wobject, new EntityInteractedEvent(entity, event.getPlayer(), Action.RIGHT_CLICK_AIR, event.getPlayer().getInventory().getItemInMainHand(), event.getClickedPosition().toLocation(entity.getWorld()))).isCancelled());
    }
}
