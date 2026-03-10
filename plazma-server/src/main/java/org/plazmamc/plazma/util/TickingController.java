package org.plazmamc.plazma.util;

import java.util.function.Function;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.plazmamc.plazma.configuration.GlobalConfiguration;

@NullMarked
public final class TickingController {

    private static @Nullable TickingController INSTANCE;
    private final GlobalConfiguration.TickControl configuration;
    private int missedTicks = 0;

    private TickingController(GlobalConfiguration.TickControl configuration) {
        this.configuration = configuration;
    }

    public static void tick() {
        if (INSTANCE == null) return;
        INSTANCE.missedTicks -= INSTANCE.missedTicks;
        INSTANCE.missedTicks += (int) (MinecraftServer.getServer().tickTimes5s.getAverage() / 50 - 1);
    }

    public static void post(GlobalConfiguration.TickControl configuration) {
        if (!configuration.enabled) {
            INSTANCE = null;
            return;
        }
        INSTANCE = new TickingController(configuration);
    }

    public static float calc(float original, Function<GlobalConfiguration.TickControl, Boolean> isAffected, boolean swap) {
        if (INSTANCE == null || !isAffected.apply(INSTANCE.configuration) || original == 0) return original;
        if (swap) return (float) Math.max(original * MinecraftServer.getServer().tps5s.getAverage() / 20, 1);
        return (float) (original * 20 / MinecraftServer.getServer().tps5s.getAverage());
    }

    public static int calc(int original, Function<GlobalConfiguration.TickControl, Boolean> isAffected, boolean swap) {
        return (int) Math.ceil(calc((float) original, isAffected, swap));
    }

    public static int missedTicks() {
        if (INSTANCE == null) return 0;
        return INSTANCE.missedTicks;
    }

    public static boolean shouldTickAgain(Block block) {
        if (INSTANCE == null || !INSTANCE.configuration.accelerate.blockEntity) return false;
        return INSTANCE.configuration.accelerate.blockEntityMaskSet.isEmpty()
                || INSTANCE.configuration.accelerate.blockEntityMaskSet.contains(block);
    }
}
