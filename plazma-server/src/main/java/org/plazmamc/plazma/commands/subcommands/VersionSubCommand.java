package org.plazmamc.plazma.commands.subcommands;

import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;
import org.plazmamc.plazma.commands.PlazmaSubCommand;

@NullMarked
public class VersionSubCommand implements PlazmaSubCommand {

    @Override
    public boolean execute(final CommandSender sender, final String subCommand, final String[] args) {
        final Command redirect = MinecraftServer.getServer().server.getCommandMap().getCommand("version");
        assert redirect != null;

        return redirect.execute(sender, "org/plazmamc/plazma", new String[0]);
    }

}
