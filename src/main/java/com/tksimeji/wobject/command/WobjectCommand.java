package com.tksimeji.wobject.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class WobjectCommand implements CommandExecutor, TabCompleter {
    private final @NotNull Map<String, Subcommand> subcommands = new HashMap<>();

    public WobjectCommand() {
        subcommand(new NewSubcommand());
    }

    private void subcommand(@NotNull Subcommand subcommand) {
        subcommands.put(subcommand.getName(), subcommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: " + command.getUsage()).color(NamedTextColor.RED));
            return true;
        }

        Subcommand subcommand = subcommands.get(args[0]);

        if (subcommand == null) {
            sender.sendMessage(Component.text("\"" + args[0] + "\" is an unknown subcommand.").color(NamedTextColor.RED));
            return true;
        }

        String[] args2 = new String[args.length - 1];
        System.arraycopy(args, 1, args2, 0, args2.length);
        subcommand.onCommand(sender, command, label, args2);
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(subcommands.keySet());
        }

        Subcommand subcommand = subcommands.get(args[0]);

        if (subcommand == null) {
            return List.of();
        }

        return Optional.ofNullable(subcommand.onTabComplete(sender, command, label, args)).orElse(List.of());
    }
}
