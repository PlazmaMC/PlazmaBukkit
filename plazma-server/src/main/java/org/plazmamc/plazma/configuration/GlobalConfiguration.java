package org.plazmamc.plazma.configuration;

import com.mojang.logging.LogUtils;
import io.papermc.paper.FeatureHooks;
import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import it.unimi.dsi.fastutil.objects.*;
import java.util.List;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal", "NotNullFieldNotInitialized", "InnerClassMayBeStatic"})
public class GlobalConfiguration extends ConfigurationPart {

    private static final Logger LOGGER = LogUtils.getLogger();
    static final int CURRENT_VERSION = 1; // (when you change the version, change the comment, so it conflicts on rebases): upgrade packet to use ids
    private static GlobalConfiguration instance;

    public static GlobalConfiguration get() {
        return instance;
    }

    static void set(final GlobalConfiguration instance) {
        GlobalConfiguration.instance = instance;
    }

    @Setting(Configuration.VERSION_FIELD)
    public int version = CURRENT_VERSION;

    public Async async;

    public class Async extends ConfigurationPart {
        public boolean mobSpawning = true;
    }

    public boolean disableMethodProfiler = true;

    public TickControl tickControl;
    public class TickControl extends ConfigurationPart {

        public boolean enabled = false;

        public Delay delay;
        public class Delay extends ConfigurationPart {

            public boolean sleep = true;
            public boolean itemUse = true;
            public boolean portalUse = true;
            public boolean lavaFluid = true;
            public boolean waterFluid = true;
            public boolean blockBreak = true;
            public boolean itemPickup = true;
            public boolean potionEffect = true;

        }

        public Accelerate accelerate;
        public class Accelerate extends ConfigurationPart {

            public boolean dayTime = true;
            public boolean randomTick = true;
            public boolean blockEntity = true;
            public List<Block> blockEntityMask = List.of();
            public transient Set<Block> blockEntityMaskSet = Set.of();

            @PostProcess
            private void postProcess() {
                this.blockEntityMaskSet = Set.copyOf(this.blockEntityMask);
            }

        }

        @PostProcess
        void post() {
            org.plazmamc.plazma.util.TickingController.post(this);
        }
    }
}