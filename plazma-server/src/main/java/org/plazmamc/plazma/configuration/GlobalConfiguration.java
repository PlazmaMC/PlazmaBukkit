package org.plazmamc.plazma.configuration;

import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@NullMarked
@SuppressWarnings({"CanBeFinal", "NotNullFieldNotInitialized", "FieldCanBeLocal", "FieldMayBeFinal", "InnerClassMayBeStatic"})
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

    @Setting(Configuration.VERSION_FIELD)
    public int version = CURRENT_VERSION;

    public Books books;
    public class Books extends ConfigurationPart {
        public boolean permissionToEdit = false;
        public boolean invertPermissionCheck = false;
        public String permission = "plazma.books";
    }
}
