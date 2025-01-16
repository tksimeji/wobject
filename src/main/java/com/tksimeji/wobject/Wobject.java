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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public final class Wobject extends JavaPlugin {
    private static final Logger log = LoggerFactory.getLogger(Wobject.class);
    private static Wobject instance;

    private static final @NotNull WobjectLoader loader = new WobjectLoader();

    public static @NotNull Wobject plugin() {
        return instance;
    }

    public static @NotNull String version() {
        return plugin().getPluginMeta().getVersion();
    }

    private static @NotNull ComponentLogger logger() {
        return plugin().getComponentLogger();
    }


    public static @Nullable Object get(@Nullable UUID uuid) {
        return all().stream()
                .filter(instance -> WobjectClass.of(instance.getClass()).getUniqueId(instance).equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public static @Nullable Object get(@Nullable Block block) {
        return all().stream()
                .filter(wobject -> WobjectClass.of(wobject.getClass()).getBlockComponent(wobject, block) != null)
                .findFirst()
                .orElse(null);
    }

    public static @Nullable Object get(@Nullable Entity entity) {
        return all().stream()
                .filter(wobject -> WobjectClass.of(wobject.getClass()).getEntityComponent(wobject, entity) != null)
                .findFirst()
                .orElse(null);
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

        logger().info(Component.text(" __      _____  ").color(TextColor.color(17, 154, 142)));
        logger().info(Component.text(" \\ \\ /\\ / / _ \\ ").color(TextColor.color(31, 183, 136)).append(Component.text("    Wobject - " + version()).color(NamedTextColor.WHITE)));
        logger().info(Component.text("  \\ V  V / (_) |").color(TextColor.color(43, 210, 131)));
        logger().info(Component.text("   \\_/\\_/ \\___/ ").color(TextColor.color(56, 239, 125)).append(Component.text("    Help poor children in Uganda!").color(NamedTextColor.GRAY)));
        logger().info(Component.text("                ").append(Component.text("    ").append(Component.text("https://iccf-holland.org/").color(NamedTextColor.BLUE).decorate(TextDecoration.UNDERLINED))));
        logger().info(Component.empty());
    }
}
