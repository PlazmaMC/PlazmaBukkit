package org.plazmamc.plazma.commands.subcommands;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.jspecify.annotations.NullMarked;
import org.plazmamc.plazma.commands.PlazmaSubCommand;

import static net.kyori.adventure.text.Component.text;
import static org.bukkit.command.Command.broadcastCommandMessage;

@NullMarked
public class ReloadSubCommand implements PlazmaSubCommand {

    @Override
    public boolean execute(final CommandSender sender, final String subCommand, final String[] args) {
        broadcastCommandMessage(sender, text("Please note that this command is not supported and may cause issues.", NamedTextColor.RED));
        broadcastCommandMessage(sender, text("If you encounter any issues please use the /stop command to restart your server.", NamedTextColor.RED));

        final MinecraftServer server = ((CraftServer) sender.getServer()).getServer();
        server.plazmaConfigurations.reloadConfigs(server);
        server.server.reloadCount++;

        broadcastCommandMessage(sender, text("Plazma config reload complete.", NamedTextColor.GREEN));
        return true;
    }

}
