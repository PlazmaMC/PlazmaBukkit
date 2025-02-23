package org.plazmamc.plazma.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jspecify.annotations.NonNull;
import org.plazmamc.plazma.Options;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public interface GCCommand {

    static void register(final @NonNull CommandDispatcher<@NonNull CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("gc")
            .requires(source -> source.hasPermission(4, "bukkit.command.gc"))
            .executes(source -> {
                System.gc();
                source.getSource().sendSuccess(miniMessage().deserialize(Options.global().messages.runGc), true);
                return 1;
            })
        );
    }

}
