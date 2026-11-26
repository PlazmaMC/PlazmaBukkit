package org.plazmamc.plazma.configuration;

import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.constraint.Constraints;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@NullMarked
@SuppressWarnings({"CanBeFinal", "NotNullFieldNotInitialized", "FieldCanBeLocal", "FieldMayBeFinal", "InnerClassMayBeStatic", "RedundantSuppression"})
public class GlobalConfiguration extends ConfigurationPart {
    static final int CURRENT_VERSION = 2; // (when you change the version, change the comment, so it conflicts on rebases): rewrite

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

    public class Misc extends ConfigurationPart {
        @Constraints.Min(0)
        public int specialDatesRefreshRate = 60 * 60 * 24; // 1 day

        @PostProcess
        private void postProcess() {
            net.minecraft.util.SpecialDates.plazma$refreshConfiguration(this.specialDatesRefreshRate * 20);
        }
    }

    public Books books;
    public class Books extends ConfigurationPart {
        public boolean permissionToEdit = false;
        public boolean invertPermissionCheck = false;
        public String permission = "plazma.books";
    }

    public Entities entities;
    public class Entities extends ConfigurationPart {
        public Projectile projectile;
        public class Projectile extends ConfigurationPart {
            public LimitChunkLoading limitChunkLoading;
            public class LimitChunkLoading extends ConfigurationPart {
                public boolean enabled = true;
                @Constraints.Min(0)
                public int maxChunkLoadsPerTick = 10;
                @Constraints.Min(0)
                public int maxChunkLoadsPerProjectile = 10;
            }
        }
    }

    public Server server;
    public class Server extends ConfigurationPart {
        private static boolean initialized = false;

        public Async async;
        public class Async extends ConfigurationPart {
            public boolean mobSpawning = true;

            void postProcess() {
                net.minecraft.server.level.ServerChunkCache.asyncMobSpawning = this.mobSpawning;
            }
        }

        @PostProcess
        private void postProcess() {
            if (initialized) {
                throw new IllegalStateException("Server configuration has already been initialized");
            }
            this.async.postProcess();
            initialized = true;
        }
    }
}
