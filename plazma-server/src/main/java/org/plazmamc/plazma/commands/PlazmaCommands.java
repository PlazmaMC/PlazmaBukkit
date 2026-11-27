package org.plazmamc.plazma.commands;

import net.minecraft.server.MinecraftServer;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlazmaCommands {

    @Contract(" -> fail")
    private PlazmaCommands() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void registerLegacyCommands(final MinecraftServer server) {
        final CommandMap commandMap = server.server.getCommandMap();

        commandMap.register("plazma", "Plazma", new PlazmaCommand("plazma"));
    }

}
