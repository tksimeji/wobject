package com.tksimeji.wobject.reflect;

import com.google.gson.JsonObject;
import com.tksimeji.wobject.api.BlockComponent;
import com.tksimeji.wobject.api.EntityComponent;
import com.tksimeji.wobject.api.Wobject;
import com.tksimeji.wobject.event.Event;
import com.tksimeji.wobject.event.Handler;
import com.tksimeji.wobject.event.KillEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public final class WobjectClass<T> implements Type {
    private static final @NotNull Set<WobjectClass<?>> instances = new HashSet<>();

    public static <T> @NotNull WobjectClass<T> of(@NotNull Class<T> clazz) {
        for (WobjectClass<?> instance : instances) {
            if (instance.getJavaClass() == clazz) {
                return (WobjectClass<T>) instance;
            }
        }

        return new WobjectClass<>(clazz);
    }

    public static @Nullable WobjectClass<?> of(@KeyPattern @Nullable String name) {
        return instances.stream().filter(instance -> instance.getKey().asString().equals(name)).findFirst().orElse(null);
    }

    public static @NotNull List<WobjectClass<?>> all() {
        return new ArrayList<>(instances);
    }

    private final @NotNull Class<T> clazz;
    private final @NotNull Wobject annotation;

    private final @NotNull Set<IWobjectComponent<?, ?, ?>> components;
    private final @NotNull Set<Method> handlers;

    private final @NotNull Map<UUID, Object> wobjects = new HashMap<>();

    private WobjectClass(@NotNull Class<T> clazz) {
        if (! com.tksimeji.wobject.Wobject.getLoader().isLoading()) {
            throw new UnsupportedOperationException();
        }

        if (! clazz.isAnnotationPresent(Wobject.class)) {
            throw new IllegalArgumentException();
        }

        if (Arrays.stream(clazz.getConstructors()).noneMatch(constructor -> constructor.getParameters().length == 0)) {
            throw new IllegalArgumentException();
        }

        this.clazz = clazz;
        annotation = clazz.getAnnotation(Wobject.class);

        if (! Key.parseable(annotation.value())) {
            throw new IllegalArgumentException();
        }

        components = getFields().stream()
                        .filter(field -> (field.isAnnotationPresent(BlockComponent.class) && Block.class.isAssignableFrom(field.getType())) ||
                                (field.isAnnotationPresent(EntityComponent.class) && Entity.class.isAssignableFrom(field.getType())))
                        .map(field -> {
                            if (field.isAnnotationPresent(BlockComponent.class)) {
                                return new WobjectBlockComponent(field);
                            } else if (field.isAnnotationPresent(EntityComponent.class)) {
                                return new WobjectEntityComponent(field);
                            }

                            throw new UnsupportedOperationException();
                        }).collect(Collectors.toSet());

        handlers = getMethods().stream()
                .filter(method -> method.isAnnotationPresent(Handler.class) &&
                        method.getParameters().length == 1 &&
                        Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                .collect(Collectors.toSet());

        instances.add(this);
    }

    public @NotNull Key getKey() {
        return Key.key(annotation.value().contains(":") ? annotation.value() : "wobject:" + annotation.value());
    }

    public @NotNull UUID getUniqueId(@NotNull Object wobject) {
        if (! wobjects.containsValue(wobject)) {
            throw new IllegalArgumentException();
        }

        return wobjects.entrySet().stream()
                .filter(entry -> entry.getValue() == wobject)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    public @Nullable Object getWobject(@NotNull UUID uuid) {
        return wobjects.get(uuid);
    }

    public @NotNull Set<Object> getWobjects() {
        return new HashSet<>(wobjects.values());
    }

    public @Nullable IWobjectComponent<?, ?, ?> getComponent(@Nullable String name) {
        return components.stream().filter(component -> component.getName().equals(name)).findFirst().orElse(null);
    }

    public @NotNull Set<IWobjectComponent<?, ?, ?>> getComponents() {
        return new HashSet<>(components);
    }

    public @Nullable WobjectBlockComponent getBlockComponent(@Nullable String name) {
        return getComponent(name) instanceof WobjectBlockComponent blockComponent ? blockComponent : null;
    }

    public @Nullable WobjectBlockComponent getBlockComponent(@Nullable Object wobject, @Nullable Block block) {
        if (wobject == null || block == null) {
            return null;
        }

        return getBlockComponents().stream().filter(component -> {
            Block value = component.getValue(wobject);
            return value != null && value.getLocation().equals(block.getLocation());
        }).findFirst().orElse(null);
    }

    public @NotNull Set<WobjectBlockComponent> getBlockComponents() {
        return components.stream()
                .filter(component -> component instanceof WobjectBlockComponent)
                .map(component -> (WobjectBlockComponent) component)
                .collect(Collectors.toSet());
    }

    public @Nullable WobjectEntityComponent getEntityComponent(@Nullable String name) {
        return getComponent(name) instanceof WobjectEntityComponent entityComponent ? entityComponent : null;
    }

    public @Nullable WobjectEntityComponent getEntityComponent(@Nullable Object wobject, @Nullable Entity entity) {
        if (wobject == null || entity == null) {
            return null;
        }

        return getEntityComponents().stream().filter(component -> {
            Entity value = component.getValue(wobject);
            return value != null && value.getUniqueId().equals(entity.getUniqueId());
        }).findFirst().orElse(null);
    }

    public @NotNull Set<WobjectEntityComponent> getEntityComponents() {
        return components.stream()
                .filter(component -> component instanceof WobjectEntityComponent)
                .map(component -> (WobjectEntityComponent) component)
                .collect(Collectors.toSet());
    }

    public @NotNull Set<Field> getFields() {
        return getJavaClassTree().stream().flatMap(clazz -> Arrays.stream(clazz.getDeclaredFields())).collect(Collectors.toSet());
    }

    public @NotNull Set<Method> getMethods() {
        return getJavaClassTree().stream().flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods())).collect(Collectors.toSet());
    }

    public @NotNull Class<T> getJavaClass() {
        return clazz;
    }

    public @NotNull Set<Class<?>> getJavaClassTree() {
        Set<Class<?>> tree = new HashSet<>();
        tree.add(clazz);

        Class<?> superclass = clazz.getSuperclass();

        while (superclass != null) {
            tree.add(superclass);
            superclass = superclass.getSuperclass();
        }

        return tree;
    }

    public @NotNull JsonObject asJsonObject(@NotNull Object wobject) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", getKey().asString());
        jsonObject.addProperty("uuid", getUniqueId(wobject).toString());

        components.forEach(component -> jsonObject.add("@" + component.getName(), component.asJsonObject(wobject)));

        return jsonObject;
    }

    public @NotNull T newInstance(@NotNull UUID uuid) {
        T wobject;

        try {
            wobject = clazz.getConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        if (com.tksimeji.wobject.Wobject.getJson().asList().stream()
                .anyMatch(element -> element.getAsJsonObject().get("uuid").getAsString().equals(uuid.toString()))) {
            com.tksimeji.wobject.Wobject.saveJson();
        }

        wobjects.put(uuid, wobject);
        return wobject;
    }

    public <E extends Event> @NotNull E call(@NotNull Object wobject, @NotNull E event) {
        return call(wobject, event, null);
    }

    public <E extends Event> @NotNull E call(@NotNull Object wobject, @NotNull E event, @Nullable IWobjectComponent<?, ?, ?> component) {
        handlers.stream()
                .filter(handler -> {
                    List<String> components = List.of(handler.getAnnotation(Handler.class).component());
                    return event.getClass().isAssignableFrom(handler.getParameterTypes()[0]) &&
                            (component == null || components.isEmpty() || components.contains(component.getName()));
                })
                .sorted(Comparator.comparingInt(handler -> handler.getAnnotation(Handler.class).priority()))
                .forEach(handler -> {
                    try {
                        handler.invoke(wobject, event);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        return event;
    }

    public void kill(@NotNull Object wobject) {
        if (of(wobject.getClass()) != this || ! wobjects.containsValue(wobject)) {
            throw new IllegalArgumentException();
        }

        call(wobject, new KillEvent());

        wobjects.remove(getUniqueId(wobject));

        getBlockComponents().forEach(component -> {
            Optional.ofNullable(component.getValue(wobject)).ifPresent(value -> value.setType(Material.AIR));
        });

        getEntityComponents().forEach(component -> {
            Optional.ofNullable(component.getValue(wobject)).ifPresent(Entity::remove);
        });

        com.tksimeji.wobject.Wobject.saveJson();
    }
}
