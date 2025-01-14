package com.tksimeji.wobject;

import com.google.gson.JsonObject;
import com.tksimeji.wobject.reflect.WobjectClass;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public final class WobjectLoader {
    public static void load() {
        Wobject.getJson().forEach(element -> mount(element.getAsJsonObject()));
        Wobject.setJson();
    }

    public static void mount(@NotNull JsonObject json) {
        WobjectClass<?> clazz = WobjectClass.of(json.get("name").getAsString());

        if (clazz == null) {
            throw new UnsupportedOperationException();
        }

        Object wobject = clazz.newInstance(UUID.fromString(json.get("uuid").getAsString()));

        clazz.getComponents().stream()
                .filter(component -> json.has("@" + component.getName()) && json.get("@" + component.getName()).isJsonObject())
                .forEach(component -> {
                    JsonObject componentJson = json.getAsJsonObject("@" + component.getName());

                    if (componentJson == null) {
                        return;
                    }

                    Block block = new Location(Bukkit.getWorld(Key.key(componentJson.get("world").getAsString())),
                            componentJson.get("x").getAsInt(),
                            componentJson.get("y").getAsInt(),
                            componentJson.get("z").getAsInt()).getBlock();

                    if (component.getTypes().contains(block.getType())) {
                        component.setValue(wobject, block);
                    }
                });
    }
}
