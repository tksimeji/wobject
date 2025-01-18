package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectEntityComponent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class EntityListener implements Listener {
    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Object wobject = Wobject.get(entity);

        if (wobject == null) {
            return;
        }

        WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());
        com.tksimeji.wobject.event.EntityDamageEvent e = new com.tksimeji.wobject.event.EntityDamageEvent(entity, event.getDamageSource(), event.getDamage());
        clazz.call(wobject, e);
        event.setDamage(e.getDamage());
        event.setCancelled(e.isCancelled());
    }

    @EventHandler
    public void onEntityMove(@NotNull EntityMoveEvent event) {
        Entity entity = event.getEntity();
        Object wobject = Wobject.get(entity);

        if (wobject == null) {
            return;
        }

        WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());
        com.tksimeji.wobject.event.EntityMoveEvent e = new com.tksimeji.wobject.event.EntityMoveEvent(entity, event.getFrom(), event.getTo());
        clazz.call(wobject, e);
        event.setFrom(e.getFrom());
        event.setTo(e.getTo());
        event.setCancelled(e.isCancelled());
    }

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

        Entity entity = event.getEntity();
        entity.setGravity(component.getAnnotation().gravity());
        entity.setSilent(component.getAnnotation().silent());
        entity.setPersistent(true);

        player.getInventory().setItemInMainHand(null);
        builder.put(component, entity);
    }
}
