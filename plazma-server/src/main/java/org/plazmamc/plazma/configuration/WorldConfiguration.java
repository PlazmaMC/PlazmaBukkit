package org.plazmamc.plazma.configuration;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.logging.LogUtils;
import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.plazmamc.plazma.Options;
import org.slf4j.Logger;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "NotNullFieldNotInitialized", "InnerClassMayBeStatic"})
public class WorldConfiguration extends ConfigurationPart {

    private static final Logger LOGGER = LogUtils.getClassLogger();
    static final int CURRENT_VERSION = 1; // (when you change the version, change the comment, so it conflicts on rebases): migrate spawn loaded configs to gamerule

    private final transient ResourceLocation worldKey;

    WorldConfiguration(final ResourceLocation worldKey) {
        this.worldKey = worldKey;
    }

    public boolean isDefault() {
        return this.worldKey.equals(PlazmaConfigurations.WORLD_DEFAULTS_KEY);
    }

    @Setting(Configuration.VERSION_FIELD)
    public int version = CURRENT_VERSION;

    public boolean optimizeVanillaShapelessRecipes = true;

    public Async async;

    public class Async extends ConfigurationPart {
        boolean mobSpawning = true;

        public boolean mobSpawning() {
            return this.mobSpawning && GlobalConfiguration.get().async.mobSpawning;
        }
    }

    public Entities entities;

    public class Entities extends ConfigurationPart {
        public boolean optimizeSuffocation = true;

        public Projectile projectile;

        public class Projectile extends ConfigurationPart {
            public int maxLoadsPerTick = 10;
            public int maxLoadsPerProjectile = Options.OPTIMIZE ? 8 : 10;
        }
    }

    public Environment environment;

    public class Environment extends ConfigurationPart {
        public boolean optimizeLightning = true;
        public boolean optimizeIceAndSnow = true;
    }

    public DynamicActivationOfBrain dab;

    public class DynamicActivationOfBrain extends ConfigurationPart {
        public boolean enabled = true;
        public int maxTickFrequency = 20;
        public int startDistance = 12;
        public int activationDistanceMod = Options.OPTIMIZE ? 7 : 8;
        public boolean disableInWater = true;
        public Reference2BooleanMap<EntityType<?>> entities = Util.make(new Reference2BooleanOpenHashMap<>(BuiltInRegistries.ENTITY_TYPE.size()), map -> {
            map.defaultReturnValue(true);
            map.put(EntityType.ARMOR_STAND, false);
        });

        public int startDistanceSquared() {
            return this.startDistance * this.startDistance;
        }

        @PostProcess
        private void postProcess() {
            for (EntityType<? extends Entity> entityType : BuiltInRegistries.ENTITY_TYPE) {
                entityType.dabEnabled = entities.getBoolean(entityType);
            }
        }
    }

    public TickRates tickRates;

    public class TickRates extends ConfigurationPart {
        public int inactiveGoalSelector = Options.OPTIMIZE ? 20 : 1;
    }
}