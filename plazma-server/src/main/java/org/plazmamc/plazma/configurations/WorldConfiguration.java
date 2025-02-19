package org.plazmamc.plazma.configurations;

import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.Configurations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public final class WorldConfiguration extends ConfigurationPart {

    static final int VERSION = 2;

    private transient final ResourceLocation worldKey;
    public WorldConfiguration(final ResourceLocation worldKey) {
        this.worldKey = worldKey;
    }

    @Setting(Configurations.VERSION_FIELD)
    int version = VERSION;

}
