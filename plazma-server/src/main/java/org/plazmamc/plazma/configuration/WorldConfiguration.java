package org.plazmamc.plazma.configuration;

import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

import static org.plazmamc.plazma.configuration.PlazmaConfigurations.LOGGER;

@NullMarked
@SuppressWarnings({"CanBeFinal", "NotNullFieldNotInitialized", "FieldCanBeLocal", "FieldMayBeFinal", "InnerClassMayBeStatic", "RedundantSuppression"})
public class WorldConfiguration extends ConfigurationPart {
    static final int CURRENT_VERSION = 2; // (when you change the version, change the comment, so it conflicts on rebases): rewrite

    private final transient Identifier worldKey;

    WorldConfiguration(final Identifier worldKey) {
        this.worldKey = worldKey;
    }

    public boolean isDefault() {
        return this.worldKey.equals(PlazmaConfigurations.WORLD_DEFAULTS_KEY);
    }

    @Setting(Configuration.VERSION_FIELD)
    public int version = CURRENT_VERSION;

    public Misc misc;
    public class Misc extends ConfigurationPart {
        public boolean optimizeShapelessBukkitRecipes = true;
        public boolean optimizeLightningCheck = true;
    }

    public Item item;
    public class Item extends ConfigurationPart {
        public EndCrystal endCrystal;
        public class EndCrystal extends ConfigurationPart {
            public boolean allowEnderDragonRespawn = true;
        }
    }

    public Entities entities;
    public class Entities extends ConfigurationPart {
        public boolean skipPathfindingForUnloadedChunks = true;

        @ApiStatus.AvailableSince("2")
        public OptimizeSuffocation optimizeSuffocation;
        public class OptimizeSuffocation extends ConfigurationPart {
            public boolean enabled = true;
            public boolean evenForWither = false;
        }

        public DynamicActivationOfTheBrains dab;
        public class DynamicActivationOfTheBrains extends ConfigurationPart {
            public boolean enabled = true;
            public boolean disableInWater = true;
            public byte startDistance = 12;
            public byte maxTickFrequency = 20;
            public byte distanceModifier = 8;

            public transient short startDistanceSquared;

            public Filter filter;
            public class Filter extends ConfigurationPart {
                public boolean asWhitelist = false;
                public List<EntityType<?>> entities = List.of(
                    EntityTypes.ARMOR_STAND,
                    EntityTypes.MANNEQUIN
                );

                private void applyConfiguration(final WorldConfiguration config) {
                    if (!config.entities.dab.enabled || this.entities.isEmpty()) return;
                    if (!config.isDefault()) {
                        LOGGER.warn("Per-world entity DAB configuration is not yet supported. The configuration for world {} will be ignored.", config.worldKey);
                        return;
                    }

                    for (final EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                        entityType.enableDAB = this.asWhitelist != this.entities.contains(entityType);
                    }
                }
            }

            @PostProcess
            private void postProcess() {
                this.startDistanceSquared = (short)(this.startDistance * this.startDistance);
            }

            private void applyConfiguration(final WorldConfiguration config) {
                if (!config.isDefault()) return;
                net.minecraft.world.entity.Mob.DEFAULT_ACTIVATED_PRIORITY = this.maxTickFrequency;
            }
        }
    }

    @PostProcess
    private void postProcess() {
        worldAwarePostProcess();
    }

    private void worldAwarePostProcess() {
        this.entities.dab.applyConfiguration(this);
        this.entities.dab.filter.applyConfiguration(this);
    }
}
