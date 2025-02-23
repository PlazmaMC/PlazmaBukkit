package org.plazmamc.plazma.configurations;

import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.Configurations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@SuppressWarnings({"unused", "InnerClassMayBeStatic", "FieldCanBeLocal"})
public final class WorldConfiguration extends ConfigurationPart {

    static final int VERSION = 2;

    private transient final ResourceLocation worldKey;
    public WorldConfiguration(final ResourceLocation worldKey) {
        this.worldKey = worldKey;
    }

    @Setting(Configurations.VERSION_FIELD)
    int version = VERSION;

    public Entity entity;
    public class Entity extends ConfigurationPart {

        public int sensorTick = 1;

        public boolean suppressErrorsFromDirtyAttributes = OPTIMIZE;
        public boolean populateLootTableOnlyForPlayerInteraction = OPTIMIZE;

        public Phantom phantom;
        public class Phantom extends ConfigurationPart {

            public boolean loadChunksToSpawn = false;

        }

    }

    public Block block;
    public class Block extends ConfigurationPart {

        public int waterFlowingTick = 5;
        public int waterDropOffSize = 1;

        public int lavaDropOffNormal = 2;
        public int lavaDropOffNether = 1;

    }

}
