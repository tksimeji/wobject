package com.tksimeji.wobject.command;

import com.tksimeji.wobject.WobjectBuilder;
import com.tksimeji.wobject.reflect.WobjectClass;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class NewSubcommand implements Subcommand {
    @Override
    public @NotNull String getName() {
        return "new";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (! (sender instanceof Player player)) {
            sender.sendMessage(Component.text("Please run this from within the game.").color(NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Component.text(String.format("Usage: /%s %s <class>", label, getName())).color(NamedTextColor.RED));
            return true;
        }

        WobjectClass<?> clazz = WobjectClass.of(args[0]);

        if (clazz == null) {
            sender.sendMessage(Component.text("\"" + args[0] + "\" is an unknown class.").color(NamedTextColor.RED));
            return true;
        }

        WobjectBuilder<?> builder = WobjectBuilder.create(clazz);

        clazz.getComponents().forEach(component -> player.getInventory().addItem(component.asItemStack(builder.getUniqueId())));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return null;
        }

        return WobjectClass.all().stream()
                .map(clazz -> clazz.getKey().asString())
                .filter(string -> string.toLowerCase().startsWith(args[0].toLowerCase()) || Key.key(string).value().toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
    }
}
