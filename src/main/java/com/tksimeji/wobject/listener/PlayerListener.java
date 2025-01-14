package com.tksimeji.wobject.listener;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.api.Handler;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectComponent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
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
        WobjectComponent component = clazz.getComponent(wobject, block);

        if (component == null) {
            return;
        }

        clazz.call(wobject, clazz.getInteractHandlers().stream().filter(handler -> {
            Handler.Interact annotation = handler.getAnnotation(Handler.Interact.class);
            List<String> components = Arrays.asList(annotation.component());
            return components.isEmpty() || components.contains(component.getName());
        }).collect(Collectors.toSet()), event, event.getPlayer(), block);
    }
}
