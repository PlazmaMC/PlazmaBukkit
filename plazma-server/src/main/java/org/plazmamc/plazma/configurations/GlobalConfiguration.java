package org.plazmamc.plazma.configurations;

import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.Configurations;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@SuppressWarnings({"unused", "InnerClassMayBeStatic"})
public final class GlobalConfiguration extends ConfigurationPart {

    static GlobalConfiguration INSTANCE;
    static final int VERSION = 2;

    @Contract(pure = true)
    public static @NonNull GlobalConfiguration get() {
        return INSTANCE;
    }

    @Setting(Configurations.VERSION_FIELD)
    int version = VERSION;

    public Player player;
    public class Player extends ConfigurationPart {

        boolean useMovedToQuicklyBypassPerm = OPTIMIZE;
        boolean checkSpectatorMovedToQuickly = !OPTIMIZE;

        int advancementCriterionTriggerIdleTick = OPTIMIZE ? 5 : 0;

        @PostProcess
        void postProcess() {
            net.minecraft.server.network.ServerGamePacketListenerImpl.useMovedToQuicklyBypassPermission = this.useMovedToQuicklyBypassPerm;
            net.minecraft.server.network.ServerGamePacketListenerImpl.checkSpectatorMovedToQuickly = this.checkSpectatorMovedToQuickly;

            net.minecraft.advancements.critereon.InventoryChangeTrigger.idleTick = this.advancementCriterionTriggerIdleTick;
        }

    }

    public Messages messages;
    public class Messages extends ConfigurationPart {

        public String runGc = "Executed garbage collectors";

        public Heal heal;
        public class Heal extends ConfigurationPart {

            public String healed = "Healed <targets>";
            public String noTargets = "No targets matched selector";

        }

    }

    public ConsoleLogs consoleLogs;
    public class ConsoleLogs extends ConfigurationPart {

        public boolean offlineWarnings = true;
        public boolean rootUserWarning = true;
        public boolean notSecurePrefix = true;

    }

}
