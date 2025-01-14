package com.tksimeji.wobject;

import com.google.gson.JsonArray;
import com.tksimeji.wobject.command.WobjectCommand;
import com.tksimeji.wobject.listener.BlockListener;
import com.tksimeji.wobject.listener.InventoryListener;
import com.tksimeji.wobject.listener.PlayerListener;
import com.tksimeji.wobject.listener.ServerListener;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.runnable.ItemStackRunnable;
import com.tksimeji.wobject.util.ResourceUtility;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public final class Wobject extends JavaPlugin {
    private static Wobject instance;

    private static final @NotNull WobjectLoader loader = new WobjectLoader();

    public static @NotNull Wobject plugin() {
        return instance;
    }

    public static @Nullable Object get(@Nullable UUID uuid) {
        return all().stream()
                .filter(instance -> WobjectClass.of(instance.getClass()).getUniqueId(instance).equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public static @Nullable Object get(@Nullable Location location) {
        return all().stream()
                .filter(wobject -> WobjectClass.of(wobject.getClass()).getComponent(wobject, location) != null)
                .findFirst()
                .orElse(null);
    }

    public static @Nullable Object get(@Nullable Block block) {
        if (block == null) {
            return null;
        }

        return get(block.getLocation());
    }

    public static @NotNull List<Object> all() {
        return WobjectClass.all().stream().flatMap(clazz -> clazz.getWobjects().stream()).toList();
    }

    public static void register(@NotNull Class<?> clazz) {
        loader.register(clazz);
    }

    public static @NotNull WobjectLoader getLoader() {
        return loader;
    }

    public static @NotNull JsonArray getJson() {
        return ResourceUtility.getJsonResource("wobject.json").getAsJsonArray();
    }

    public static void setJson(@NotNull JsonArray json) {
        ResourceUtility.setJsonResource("wobject.json", json);
    }

    public static void saveJson() {
        JsonArray json = new JsonArray();

        for (WobjectClass<?> clazz : WobjectClass.all()) {
            for (Object wobject : clazz.getWobjects()) {
                json.add(clazz.asJsonObject(wobject));
            }
        }

        setJson(json);
    }

    @Override
    public void onEnable() {
        instance = this;

        getCommand("wobject").setExecutor(new WobjectCommand());

        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ServerListener(), this);

        new ItemStackRunnable().runTaskTimerAsynchronously(this, 0L, 16L);

        ResourceUtility.newResource("wobject.json", false);
    }
}
