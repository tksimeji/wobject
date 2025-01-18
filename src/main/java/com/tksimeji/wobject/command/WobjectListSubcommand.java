package com.tksimeji.wobject.command;

import com.tksimeji.wobject.Wobject;
import com.tksimeji.wobject.reflect.WobjectBlockComponent;
import com.tksimeji.wobject.reflect.WobjectClass;
import com.tksimeji.wobject.reflect.WobjectEntityComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class WobjectListSubcommand implements Subcommand {
    @Override
    public @NotNull String getName() {
        return "wobject-list";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            sender.sendMessage(Component.text(String.format("Usage: /%s %s", label, getName())).color(NamedTextColor.RED));
            return true;
        }

        List<Object> wobjects = Wobject.all();

        sender.sendMessage("Wobjects (" + wobjects.size() + "):");

        if (wobjects.isEmpty()) {
            sender.sendMessage(Component.text("This is empty.").color(NamedTextColor.RED));
            return true;
        }

        for (Object wobject : wobjects) {
            WobjectClass<?> clazz = WobjectClass.of(wobject.getClass());

            Component hashCode;

            try {
                hashCode = Component.text(wobject.hashCode()).color(NamedTextColor.DARK_GRAY);
            } catch (Exception ignored) {
                hashCode = Component.text("Failed to get").color(NamedTextColor.RED);
            }

            Component toString = null;

            try {
                toString = Component.text(wobject.toString()).color(NamedTextColor.DARK_GRAY);
            } catch (Exception ignored) {
                toString = Component.text("Failed to get").color(NamedTextColor.RED);
            }

            sender.sendMessage(Component.text(clazz.getUniqueId(wobject).toString()).color(TextColor.color(2, 134, 206)));
            sender.sendMessage(Component.text(" - ").color(NamedTextColor.DARK_GRAY).append(Component.text("Wobject class: " + clazz.getKey().asString()).color(NamedTextColor.GRAY)));
            sender.sendMessage(Component.text(" - ").color(NamedTextColor.DARK_GRAY).append(Component.text(wobject.getClass() + "#hashCode(): ").color(NamedTextColor.GRAY)).append(hashCode));
            sender.sendMessage(Component.text(" - ").color(NamedTextColor.DARK_GRAY).append(Component.text(wobject.getClass() + "#toString(): ").color(NamedTextColor.GRAY)).append(toString));

            clazz.getComponents().stream()
                    .filter(component -> component.hasValue(wobject))
                    .forEach(component -> {
                        Location location;

                        if (component instanceof WobjectBlockComponent blockComponent) {
                            location = Objects.requireNonNull(blockComponent.getValue(wobject)).getLocation();
                        } else if (component instanceof WobjectEntityComponent entityComponent) {
                            location = Objects.requireNonNull(entityComponent.getValue(wobject)).getLocation();
                        } else {
                            return;
                        }

                        sender.sendMessage(Component.text(" - ").color(NamedTextColor.DARK_GRAY)
                                .append(Component.text(component.getName()).color(NamedTextColor.WHITE))
                                .append(Component.text(String.format("%s, %s, %s, %s", location.getWorld().getKey().asString(), location.getX(), location.getY(), location.getZ())).color(NamedTextColor.GRAY)
                                        .hoverEvent(Component.text("Click to teleport."))
                                        .clickEvent(ClickEvent.runCommand(String.format("/minecraft:execute in %s run tp @p %s %s %s", location.getWorld().getKey().asString(), location.getX(), location. getY(), location.getZ())))));
                    });
        }

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
