package com.tksimeji.wobject;

import com.google.gson.JsonArray;
import com.tksimeji.wobject.command.WobjectCommand;
import com.tksimeji.wobject.listener.BlockListener;
import com.tksimeji.wobject.listener.InventoryListener;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.runnable.ItemStackRunnable;
import com.tksimeji.wobject.test.SampleWobject;
import com.tksimeji.wobject.util.ResourceUtility;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Wobject extends JavaPlugin {
    private static Wobject instance;

    public static @NotNull Wobject plugin() {
        return instance;
    }

    public static @NotNull JsonArray getJson() {
        return ResourceUtility.getJsonResource("wobject.json").getAsJsonArray();
    }

    public static void setJson() {
        JsonArray json = new JsonArray();

        for (WobjectClass<?> clazz : WobjectClass.all()) {
            for (Object wobject : clazz.getWobjects()) {
                json.add(clazz.asJsonObject(wobject));
            }
        }

        setJson(json);
    }

    public static void setJson(@NotNull JsonArray json) {
        ResourceUtility.setJsonResource("wobject.json", json);
    }

    @Override
    public void onEnable() {
        instance = this;

        getCommand("wobject").setExecutor(new WobjectCommand());

        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        ResourceUtility.newResource("wobject.json", false);

        new ItemStackRunnable().runTaskTimerAsynchronously(this, 0L, 16L);

        WobjectClass.of(SampleWobject.class);

        WobjectLoader.load();
    }
}
