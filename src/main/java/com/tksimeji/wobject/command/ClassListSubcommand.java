package com.tksimeji.wobject.command;

import com.tksimeji.wobject.reflect.WobjectClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ClassListSubcommand implements Subcommand {
    @Override
    public @NotNull String getName() {
        return "class-list";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            sender.sendMessage(Component.text(String.format("Usage: /%s %s", label, getName())).color(NamedTextColor.RED));
            return true;
        }

        List<WobjectClass<?>> classes = WobjectClass.all();

        sender.sendMessage("Wobject Classes (" + classes.size() + "):");

        if (classes.isEmpty()) {
            sender.sendMessage(Component.text("This is empty.").color(NamedTextColor.RED));
            return true;
        }

        for (WobjectClass<?> clazz : classes) {
            sender.sendMessage(Component.text(clazz.getKey().asString()).color(TextColor.color(233, 127, 6)));
            sender.sendMessage(Component.text(" - ").color(NamedTextColor.DARK_GRAY).append(Component.text("Java class: " + clazz.getJavaClass().getName()).color(NamedTextColor.GRAY)));
            sender.sendMessage(Component.text(" - ").color(NamedTextColor.DARK_GRAY).append(Component.text("Component(s): " + clazz.getComponents().size()).color(NamedTextColor.GRAY)));
        }

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
