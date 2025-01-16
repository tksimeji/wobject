package com.tksimeji.wobject;

import com.google.gson.JsonObject;
import com.tksimeji.wobject.reflect.WobjectBlockComponent;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectEntityComponent;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class WobjectLoader {
    private static final Logger log = LoggerFactory.getLogger(WobjectLoader.class);
    private final @NotNull Map<Key, Class<?>> map = new HashMap<>();

    private boolean freeze = false;
    private boolean loading = false;

    WobjectLoader() {
    }

    public void register(@NotNull Class<?> clazz) {
        if (freeze) {
            throw new UnsupportedOperationException("The registry is frozen and no further changes can be made.");
        }

        if (! clazz.isAnnotationPresent(com.tksimeji.wobject.api.Wobject.class)) {
            throw new IllegalArgumentException("The Wobject class must be annotated with \"com.tksimeji.wobject.api.Wobject\".");
        }

        if (Arrays.stream(clazz.getConstructors()).noneMatch(constructor -> constructor.getParameters().length == 0)) {
            throw new IllegalArgumentException("The Wobject class must have a constructor that takes no arguments.");
        }

        com.tksimeji.wobject.api.Wobject annotation = clazz.getAnnotation(com.tksimeji.wobject.api.Wobject.class);

        if (! Key.parseable(annotation.value())) {
            throw new IllegalArgumentException("\"" + annotation.value() + "\" is an invalid key.");
        }

        Key key = Key.key(annotation.value().contains(":") ? annotation.value() : "wobject:" + annotation.value());

        if (map.containsKey(key)) {
            throw new IllegalStateException("The key \"" + key.asString() + "\" is already registered.");
        }

        map.put(key, clazz);
    }

    public void freeze() {
        freeze = true;
    }

    public void load() {
        loading = true;
        freeze();
        map.forEach((key, value) -> WobjectClass.of(value));

        Wobject.getJson().asList().forEach(element -> load(element.getAsJsonObject()));
        Wobject.saveJson();

        loading = false;
    }

    private void load(@NotNull JsonObject json) {
        WobjectClass<?> clazz = WobjectClass.of(json.get("name").getAsString());

        if (clazz == null) {
            return;
        }

        UUID uuid = UUID.fromString(json.get("uuid").getAsString());

        if (Wobject.get(uuid) != null) {
            return;
        }

        Object wobject = null;

        for (WobjectBlockComponent component : clazz.getBlockComponents()) {
            JsonObject componentJson = json.getAsJsonObject("@" + component.getName());

            if (componentJson == null) {
                return;
            }

            Block block = new Location(Bukkit.getWorld(Key.key(componentJson.get("world").getAsString())),
                    componentJson.get("x").getAsInt(),
                    componentJson.get("y").getAsInt(),
                    componentJson.get("z").getAsInt()).getBlock();

            if (! component.getTypes().contains(block.getType())) {
                return;
            }

            if (wobject == null) {
                wobject = clazz.newInstance(uuid);
            }

            component.setValue(wobject, block);
        }

        for (WobjectEntityComponent component : clazz.getEntityComponents()) {
            JsonObject componentJson = json.getAsJsonObject("@" + component.getName());

            if (componentJson == null) {
                return;
            }

            Entity entity = Bukkit.getEntity(UUID.fromString(componentJson.get("uuid").getAsString()));

            if (entity == null || ! component.getTypes().contains(entity.getType())) {
                return;
            }

            if (wobject == null) {
                wobject = clazz.newInstance(uuid);
            }

            component.setValue(wobject, entity);
        }
    }

    public boolean isFroze() {
        return freeze;
    }

    public boolean isLoading() {
        return loading;
    }
}
