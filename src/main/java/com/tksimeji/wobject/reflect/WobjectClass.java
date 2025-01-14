package com.tksimeji.wobject.reflect;

import com.google.gson.JsonObject;
import com.tksimeji.wobject.api.Component;
import com.tksimeji.wobject.api.Handler;
import com.tksimeji.wobject.api.Wobject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public final class WobjectClass<T> implements Type {
    private static @NotNull Set<WobjectClass<?>> instances = new HashSet<>();

    public static <T> @NotNull WobjectClass<T> of(@NotNull Class<T> clazz) {
        for (WobjectClass<?> instance : instances) {
            if (instance.getJavaClass() == clazz) {
                return (WobjectClass<T>) instance;
            }
        }

        return new WobjectClass<>(clazz);
    }

    public static @Nullable WobjectClass<?> of(@Nullable String name) {
        return instances.stream().filter(instance -> instance.getName().equals(name)).findFirst().orElse(null);
    }

    public static @NotNull Set<WobjectClass<?>> all() {
        return new HashSet<>(instances);
    }

    private final @NotNull Class<T> clazz;
    private final @NotNull Wobject annotation;

    private final @NotNull Set<WobjectComponent> components;

    private final @NotNull Set<Method> interactHandlers;
    private final @NotNull Set<Method> redstoneHandlers;
    private final @NotNull Set<Method> killHandlers;

    private final @NotNull Map<UUID, Object> wobjects = new HashMap<>();

    private WobjectClass(@NotNull Class<T> clazz) {
        if (! clazz.isAnnotationPresent(Wobject.class)) {
            throw new IllegalArgumentException();
        }

        if (Arrays.stream(clazz.getConstructors()).noneMatch(constructor -> constructor.getParameters().length == 0)) {
            throw new IllegalArgumentException();
        }

        this.clazz = clazz;
        annotation = clazz.getAnnotation(Wobject.class);
        components = getFields().stream()
                        .filter(field -> field.isAnnotationPresent(Component.class) && Block.class.isAssignableFrom(field.getType()))
                        .map(WobjectComponent::new)
                        .collect(Collectors.toSet());

        interactHandlers = getMethods().stream()
                        .filter(method -> method.isAnnotationPresent(Handler.Interact.class))
                        .collect(Collectors.toSet());

        redstoneHandlers = getMethods().stream()
                        .filter(method -> method.isAnnotationPresent(Handler.Redstone.class))
                        .collect(Collectors.toSet());

        killHandlers = getMethods().stream()
                        .filter(method -> method.isAnnotationPresent(Handler.Kill.class))
                        .collect(Collectors.toSet());

        instances.add(this);
    }

    public @NotNull String getName() {
        return annotation.name();
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

    public @Nullable WobjectComponent getComponent(@Nullable String name) {
        return components.stream().filter(component -> component.getName().equals(name)).findFirst().orElse(null);
    }

    public @Nullable WobjectComponent getComponent(@Nullable Object wobject, @Nullable Location location) {
        if (wobject == null || location == null) {
            return null;
        }

        return components.stream().filter(component -> {
            Block block = component.getValue(wobject);
            return block != null && block.getLocation().equals(location);
        }).findFirst().orElse(null);
    }

    public @Nullable WobjectComponent getComponent(@Nullable Object wobject, @Nullable Block block) {
        if (block == null) {
            return null;
        }

        return getComponent(wobject, block.getLocation());
    }

    public @NotNull Set<WobjectComponent> getComponents() {
        return new HashSet<>(components);
    }

    public @NotNull Set<Method> getInteractHandlers() {
        return interactHandlers;
    }

    public @NotNull Set<Method> getRedstoneHandlers() {
        return redstoneHandlers;
    }

    public @NotNull Set<Method> getKillHandlers() {
        return killHandlers;
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
        jsonObject.addProperty("name", getName());
        jsonObject.addProperty("uuid", getUniqueId(wobject).toString());

        components.forEach(component -> jsonObject.add("@" + component.getName(), component.asJsonObject(wobject)));

        return jsonObject;
    }

    public @NotNull T newInstance(@NotNull UUID uuid) {
        T wobject;

        try {
            wobject = clazz.getConstructor().newInstance();
            wobjects.put(uuid, wobject);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        if (com.tksimeji.wobject.Wobject.getJson().asList().stream()
                .anyMatch(element -> element.getAsJsonObject().get("uuid").getAsString().equals(uuid.toString()))) {
            com.tksimeji.wobject.Wobject.setJson();
        }

        return wobject;
    }

    public void call(@NotNull Object wobject, @NotNull Set<Method> handlers, @NotNull Object... args) {
        if (of(wobject.getClass()) != this || ! wobjects.containsValue(wobject)) {
            throw new IllegalArgumentException();
        }

        for (Method handler : handlers) {
            handler.setAccessible(true);

            Parameter[] parameters = handler.getParameters();
            Object[] assembledArgs = new Object[parameters.length];

            for (int i = 0; i < assembledArgs.length; i ++) {
                Parameter parameter = parameters[i];
                Class<?> type = parameter.getType();

                int finalI = i;

                Arrays.stream(args)
                        .filter(arg -> type.isAssignableFrom(arg.getClass()))
                        .findFirst()
                        .ifPresentOrElse(arg -> assembledArgs[finalI] = arg, () -> assembledArgs[finalI] = null);
            }

            try {
                handler.invoke(wobject, assembledArgs);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void kill(@NotNull Object wobject) {
        if (of(wobject.getClass()) != this || ! wobjects.containsValue(wobject)) {
            throw new IllegalArgumentException();
        }

        call(wobject, getKillHandlers());

        wobjects.remove(getUniqueId(wobject));
        getComponents().forEach(component -> {
            Block block = component.getValue(wobject);

            if (block != null) {
                block.setType(Material.AIR);
            }
        });

        com.tksimeji.wobject.Wobject.setJson();
    }
}
