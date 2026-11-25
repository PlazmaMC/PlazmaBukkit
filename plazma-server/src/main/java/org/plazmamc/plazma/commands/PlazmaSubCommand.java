package org.plazmamc.plazma.commands;

import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;

@NullMarked
public interface PlazmaSubCommand {

    boolean execute(final CommandSender sender, final String subCommand, final String[] args);

    default List<String> tabComplete(final CommandSender sender, final String subCommand, final String[] args) {
        return Collections.emptyList();
    }

}
