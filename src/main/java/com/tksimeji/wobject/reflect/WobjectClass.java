package com.tksimeji.wobject.reflect;

import com.google.gson.JsonObject;
import com.tksimeji.wobject.api.Component;
import com.tksimeji.wobject.api.Wobject;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public final class WobjectClass<T> implements Type {
    private static @NotNull Set<WobjectClass<?>> instances = new HashSet<>();

    public static <T> @NotNull WobjectClass<T> of(@NotNull Class<T> clazz) {
        return instances.stream()
                .filter(instance -> instance.getJavaClass() == clazz)
                .map(instance -> (WobjectClass<T>) instance)
                .findFirst()
                .orElse(new WobjectClass<>(clazz));
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

    public @Nullable WobjectComponent getComponent(@NotNull String name) {
        return components.stream().filter(component -> component.getName().equals(name)).findFirst().orElse(null);
    }

    public @NotNull Set<WobjectComponent> getComponents() {
        return new HashSet<>(components);
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

    public void kill(@NotNull Object wobject) {
        if (! wobjects.containsValue(wobject)) {
            throw new IllegalArgumentException();
        }

        wobjects.remove(getUniqueId(wobject));
    }
}
