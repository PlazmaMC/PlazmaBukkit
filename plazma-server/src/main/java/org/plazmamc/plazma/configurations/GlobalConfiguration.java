package org.plazmamc.plazma.configurations;

import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.Configurations;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@SuppressWarnings("unused")
public final class GlobalConfiguration extends ConfigurationPart {

    static GlobalConfiguration INSTANCE;
    static final int VERSION = 2;

    @Contract(pure = true)
    public static @NonNull GlobalConfiguration get() {
        return INSTANCE;
    }

    @Setting(Configurations.VERSION_FIELD)
    int version = VERSION;

}
