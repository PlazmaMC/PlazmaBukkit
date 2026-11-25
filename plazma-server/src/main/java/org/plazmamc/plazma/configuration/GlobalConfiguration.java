package org.plazmamc.plazma.configuration;

import io.papermc.paper.configuration.ConfigurationPart;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class GlobalConfiguration extends ConfigurationPart {
    static final int CURRENT_VERSION = 1; // (when you change the version, change the comment, so it conflicts on rebases): rewrite

    @Nullable
    private static GlobalConfiguration INSTANCE;

    @Contract(pure = true)
    public static GlobalConfiguration get() {
        assert INSTANCE != null;
        return INSTANCE;
    }

    static void set(final GlobalConfiguration instance) {
        INSTANCE = instance;
    }
}
