package org.plazmamc.plazma.command.subcommands;

import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.plazmamc.plazma.command.PlazmaSubcommand;

@DefaultQualifier(NonNull.class)
public final class VersionCommand implements PlazmaSubcommand {
    @Override
    public boolean execute(final CommandSender sender, final String subCommand, final String[] args) {
        final @Nullable Command redirect = MinecraftServer.getServer().server.getCommandMap().getCommand("version");
        if (redirect != null) {
            redirect.execute(sender, "org/plazmamc/plazma", new String[0]);
        }
        return true;
    }
}
