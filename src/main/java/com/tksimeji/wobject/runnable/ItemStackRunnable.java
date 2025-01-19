package com.tksimeji.wobject.runnable;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.reflect.IWobjectComponent;
import com.tksimeji.wobject.reflect.WobjectClass;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public final class ItemStackRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inventory = player.getInventory();
            int index = -1;

            for (ItemStack itemStack : inventory) {
                index ++;

                if (index == inventory.getHeldItemSlot()) {
                    continue;
                }

                if (itemStack == null || ! itemStack.hasItemMeta()) {
                    continue;
                }

                PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

                if (! container.has(new NamespacedKey(Wobject.plugin(), "class"))) {
                    continue;
                }

                WobjectClass<?> clazz = WobjectClass.of(container.get(new NamespacedKey(Wobject.plugin(), "class"), PersistentDataType.STRING));

                if (clazz == null) {
                    continue;
                }


                IWobjectComponent<?, ?, ?> component = clazz.getComponent(Objects.requireNonNull(container.get(new NamespacedKey(Wobject.plugin(), "component"), PersistentDataType.STRING)));
                UUID uuid = UUID.fromString(Objects.requireNonNull(container.get(new NamespacedKey(Wobject.plugin(), "builder"), PersistentDataType.STRING)));

                WobjectBuilder<?> builder = WobjectBuilder.get(uuid);

                if (builder == null) {
                    inventory.clear(index);
                    continue;
                }

                if (component == null || component.getTypes().size() == 1) {
                    continue;
                }

                int texture = (component.getTypes().indexOf(itemStack.getType()) + 1) % component.getTypes().size();

                inventory.setItem(index, component.asItemStack(uuid, texture));
            }
        }
    }
}
