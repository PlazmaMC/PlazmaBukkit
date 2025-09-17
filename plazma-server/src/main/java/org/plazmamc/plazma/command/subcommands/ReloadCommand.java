package org.plazmamc.plazma.command.subcommands;

import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.plazmamc.plazma.command.PlazmaSubcommand;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

@DefaultQualifier(NonNull.class)
public final class ReloadCommand implements PlazmaSubcommand {
    @Override
    public boolean execute(final CommandSender sender, final String subCommand, final String[] args) {
        this.doReload(sender);
        return true;
    }

    private void doReload(final CommandSender sender) {
        Command.broadcastCommandMessage(sender, text("Please note that this command is not supported and may cause issues.", RED));
        Command.broadcastCommandMessage(sender, text("If you encounter any issues please use the /stop command to restart your server.", RED));

        MinecraftServer server = ((CraftServer) sender.getServer()).getServer();
        server.plazmaConfigurations.reloadConfigs(server);
        server.server.reloadCount++;

        Command.broadcastCommandMessage(sender, text("Plazma config reload complete.", GREEN));
    }
}
