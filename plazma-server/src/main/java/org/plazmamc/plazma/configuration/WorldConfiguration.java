package org.plazmamc.plazma.configuration;

import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.objectmapping.meta.Setting;

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

    public Entities entities;
    public class Entities extends ConfigurationPart {
        @ApiStatus.AvailableSince("2")
        public OptimizeSuffocation optimizeSuffocation;
        public class OptimizeSuffocation extends ConfigurationPart {
            public boolean enabled = true;
            public boolean evenForWither = false;
        }
    }
}
