package org.plazmamc.plazma.commands;

import net.minecraft.server.MinecraftServer;
import org.bukkit.command.CommandMap;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum PlazmaCommands {
    ;

    public static void registerLegacyCommands(final MinecraftServer server) {
        final CommandMap commandMap = server.server.getCommandMap();

        commandMap.register("plazma", "Plazma", new PlazmaCommand("plazma"));
    }

}
