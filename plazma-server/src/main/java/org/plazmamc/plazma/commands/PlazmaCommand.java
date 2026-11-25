package org.plazmamc.plazma.commands;

import io.papermc.paper.command.CommandUtil;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.plazmamc.plazma.commands.subcommands.ReloadSubCommand;
import org.plazmamc.plazma.commands.subcommands.VersionSubCommand;

import java.util.*;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

@NullMarked
final class PlazmaCommand extends Command {

    private static final String BASE_PERMISSION = "bukkit.command.plazma";

    private static final Map<String, PlazmaSubCommand> SUB_COMMANDS = Map.of(
        "reload", new ReloadSubCommand(),
        "version", new VersionSubCommand()
    );

    private static final Map<String, String> ALIASES = Map.of(
        "reload", Set.of("r", "rl"),
        "version", Set.of("v", "ver")
    )
        .entrySet().stream()
        .flatMap(entry -> entry.getValue().stream().map(alias -> Map.entry(alias, entry.getKey())))
        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

    PlazmaCommand(final String name) {
        super(name);

        this.description = "Plazma related commands";
        this.usageMessage = String.format("/plazma [%s]", String.join("|", SUB_COMMANDS.keySet()));

        final List<String> permissions = new ArrayList<>();
        permissions.add(BASE_PERMISSION);
        for (final String subCommand : SUB_COMMANDS.keySet())
            permissions.add(BASE_PERMISSION + "." + subCommand);

        this.setPermission(String.join(";", permissions));

        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        for (final String permission : permissions)
            pluginManager.addPermission(new Permission(permission, PermissionDefault.OP));
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!testPermission(sender)) return false;

        if (args.length == 0) {
            sender.sendMessage(text("Usage: " + this.usageMessage, NamedTextColor.RED));
            return false;
        }

        final Pair<String, PlazmaSubCommand> subCommandPair = resolveSubCommand(args[0]);
        if (subCommandPair == null) {
            sender.sendMessage(text("Usage: " + this.usageMessage, NamedTextColor.RED));
            return false;
        }

        if (!testSubCommandPermissionSilently(sender, subCommandPair.first())) {
            sender.sendMessage(Bukkit.permissionMessage());
            return false;
        }

        return subCommandPair.second()
            .execute(sender, subCommandPair.first(), Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> tabComplete(
        final CommandSender sender,
        final String aliases,
        final String[] args
    ) throws IllegalArgumentException {
        if (args.length <= 1)
            return CommandUtil.getListMatchingLast(sender, args, SUB_COMMANDS.keySet(), BASE_PERMISSION);

        final Pair<String, PlazmaSubCommand> subCommandPair = resolveSubCommand(args[0]);

        if (subCommandPair != null && testSubCommandPermissionSilently(sender, subCommandPair.first()))
            return subCommandPair.second()
                .tabComplete(sender, subCommandPair.first(), Arrays.copyOfRange(args, 1, args.length));
        return Collections.emptyList();
    }

    private static boolean testSubCommandPermissionSilently(final CommandSender sender, final String subCommand) {
        return sender.hasPermission(BASE_PERMISSION) || sender.hasPermission(BASE_PERMISSION + "." + subCommand);
    }

    private static @Nullable Pair<String, PlazmaSubCommand> resolveSubCommand(String label) {
        label = label.toLowerCase(Locale.ENGLISH);
        PlazmaSubCommand subCommand = SUB_COMMANDS.get(label);

        if (subCommand == null) {
            label = ALIASES.get(label);
            if (label == null) return null;

            subCommand = SUB_COMMANDS.get(label);
        }

        return subCommand != null ? Pair.of(label, subCommand) : null;
    }

}
