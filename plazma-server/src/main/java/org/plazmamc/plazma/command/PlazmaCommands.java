package org.plazmamc.plazma.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.PaperPluginsCommand;
import io.papermc.paper.command.PaperVersionCommand;
import io.papermc.paper.command.brigadier.CommandRegistrationFlag;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DefaultQualifier(NonNull.class)
public final class PlazmaCommands {

    private PlazmaCommands() {
    }

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    public static void registerCommands(final MinecraftServer server) {
        COMMANDS.put("org/plazmamc/plazma", new PlazmaCommand("org/plazmamc/plazma"));

        COMMANDS.forEach((s, command) -> {
            server.server.getCommandMap().register(s, "Plazma", command);
        });
    }

    public static void registerCommands() {
        // Paper commands go here
        registerInternalCommand(PaperVersionCommand.create(), "bukkit", PaperVersionCommand.DESCRIPTION, List.of("ver", "about"), Set.of());
        registerInternalCommand(PaperPluginsCommand.create(), "bukkit", PaperPluginsCommand.DESCRIPTION, List.of("pl"), Set.of());
    }

    private static void registerInternalCommand(final LiteralCommandNode<CommandSourceStack> node, final String namespace, final String description, final List<String> aliases, final Set<CommandRegistrationFlag> flags) {
        io.papermc.paper.command.brigadier.PaperCommands.INSTANCE.registerWithFlagsInternal(
            null,
            namespace,
            "Plazma",
            node,
            description,
            aliases,
            flags
        );
    }
}
