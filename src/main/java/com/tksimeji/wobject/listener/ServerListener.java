package com.tksimeji.wobject.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.event.TickEvent;
import com.tksimeji.wobject.reflect.WobjectClass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

public final class ServerListener implements Listener {
    @EventHandler
    public void onServerLoad(@NotNull ServerLoadEvent event) {
        if (event.getType() != ServerLoadEvent.LoadType.STARTUP) {
            return;
        }

        Wobject.getLoader().load();
    }

    @EventHandler
    public void onServerTickStart(@NotNull ServerTickStartEvent event) {
        Wobject.all().forEach(wobject -> {
            WobjectClass.of(wobject.getClass()).call(wobject, new TickEvent(event.getTickNumber()));
        });
    }
}
