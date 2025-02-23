package org.plazmamc.plazma.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.plazmamc.plazma.Options;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class HealCommand {

    @Contract(value = " -> fail", pure = true)
    private HealCommand() {
        throw new UnsupportedOperationException();
    }

    public static void register(final @NonNull CommandDispatcher<@NonNull CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("heal")
            .requires(source -> source.hasPermission(2, "bukkit.command.heal"))
            .executes(ctx -> execute(ctx.getSource(), Collections.singleton(ctx.getSource().getEntityOrException())))
            .then(Commands.argument("targets", EntityArgument.entities())
                .requires(source -> source.hasPermission(3, "bukkit.command.heal.others"))
                .executes(ctx -> execute(ctx.getSource(), EntityArgument.getEntities(ctx, "targets")))
            )
        );
    }

    @Contract(pure = true)
    private static int execute(final @NonNull CommandSourceStack sender, final @NonNull Collection<? extends Entity> targets) throws CommandSyntaxException {
        final ArrayList<Component> success = new ArrayList<>();

        for (Entity target : targets) {
            if (!(target instanceof LivingEntity entity)) continue;
            if (entity.isDeadOrDying()) continue;

            entity.heal(entity.getMaxHealth());
            success.add(PaperAdventure.asAdventure(entity.getDisplayName()));

            if (!(entity instanceof Player player)) continue;
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(5.0F);
        }

        if (success.isEmpty()) {
            throw new SimpleCommandExceptionType(PaperAdventure.asVanilla(miniMessage().deserialize(Options.global().messages.heal.noTargets))).create();
        }

        final Component successJoined = success.stream().reduce((a, b) -> a.append(Component.text(", ").append(b))).orElseThrow();

        sender.sendSuccess(miniMessage().deserialize(Options.global().messages.heal.healed, Placeholder.component("targets", successJoined)));
        return success.size();
    }

}
