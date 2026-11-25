package org.plazmamc.plazma.configuration;

import io.papermc.paper.configuration.ConfigurationPart;
import net.minecraft.resources.Identifier;

public class WorldConfiguration extends ConfigurationPart {
    static final int CURRENT_VERSION = 1; // (when you change the version, change the comment, so it conflicts on rebases): rewrite

    private final transient Identifier worldKey;

    WorldConfiguration(final Identifier worldKey) {
        this.worldKey = worldKey;
    }

    public boolean isDefault() {
        return this.worldKey.equals(PlazmaConfigurations.WORLD_DEFAULTS_KEY);
    }
}
