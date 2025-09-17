package org.plazmamc.plazma.configuration;

import io.papermc.paper.configuration.Configurations;
import io.papermc.paper.configuration.Metadata;
import io.papermc.paper.configuration.serializer.ComponentSerializer;
import io.papermc.paper.configuration.serializer.EnumValueSerializer;
import io.papermc.paper.configuration.serializer.ResourceLocationSerializer;
import io.papermc.paper.configuration.serializer.collection.map.MapSerializer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationOptions;

import java.nio.file.Path;
import java.util.function.Function;

@DefaultQualifier(NonNull.class)
public final class PlazmaConfigurations extends Configurations<GlobalConfiguration, WorldConfiguration> {

    private static final String GLOBAL_CONFIG_FILE_NAME = "plazma-global.yml";
    private static final String WORLD_DEFAULTS_CONFIG_FILE_NAME = "plazma-world-defaults.yml";
    private static final String WORLD_CONFIG_FILE_NAME = "plazma-world.yml";

    private static final String GLOBAL_HEADER = String.format("""
            This is the global configuration file for Plazma.
            As you can see, there's a lot to configure. Some options may impact gameplay, so use
            with caution, and make sure you know what each option does before configuring.
            
            If you need help with the configuration or have any questions related to Plazma,
            join us in our Discord or check the docs page.
            
            The world configuration options have been moved inside
            their respective world folder. The files are named %s
            
            File Reference: https://docs.plazmamc.org/plazma/reference/world-configuration/
            Docs: https://docs.plazmamc.org/
            GitHub: https://github.com/PlazmaMC/Plazma
            Support: https://plazmamc.org/discord""", WORLD_CONFIG_FILE_NAME);

    private static final String WORLD_DEFAULTS_HEADER = """
            This is the world defaults configuration file for Plazma.
            As you can see, there's a lot to configure. Some options may impact gameplay, so use
            with caution, and make sure you know what each option does before configuring.
            
            If you need help with the configuration or have any questions related to Plazma,
            join us in our Discord or check the docs page.
            
            Configuration options here apply to all worlds, unless you specify overrides inside
            the world-specific config file inside each world folder.
            
            File Reference: https://docs.plazmamc.org/plazma/reference/world-configuration/
            Docs: https://docs.plazmamc.org/
            GitHub: https://github.com/PlazmaMC/Plazma
            Support: https://plazmamc.org/discord""";

    private static final Function<ContextMap, String> WORLD_HEADER = map -> String.format("""
                    This is a world configuration file for Plazma.
                    This file may start empty but can be filled with settings to override ones in the %s/%s
                    
                    For more information, see https://docs.papermc.io/paper/reference/configuration/#per-world-configuration
                    
                    World: %s (%s)""",
            PlazmaConfigurations.CONFIG_DIR,
            PlazmaConfigurations.WORLD_DEFAULTS_CONFIG_FILE_NAME,
            map.require(WORLD_NAME),
            map.require(WORLD_KEY)
    );

    private static final Metadata.Global<GlobalConfiguration> GLOBAL_METADATA = Metadata.Global.of(
            GlobalConfiguration.class,
            GLOBAL_CONFIG_FILE_NAME,
            GlobalConfiguration.CURRENT_VERSION,
            GLOBAL_HEADER
    );

    private static final Metadata.World<WorldConfiguration> WORLD_METADATA = Metadata.World.of(
            WorldConfiguration.class,
            WORLD_CONFIG_FILE_NAME,
            WorldConfiguration.CURRENT_VERSION,
            WORLD_HEADER,
            WORLD_DEFAULTS_CONFIG_FILE_NAME,
            WORLD_DEFAULTS_HEADER
    );

    PlazmaConfigurations(final Path folder) {
        super(folder, GLOBAL_METADATA, WORLD_METADATA);
    }

    @Override
    protected GlobalConfiguration globalConfigInstance() {
        return GlobalConfiguration.get();
    }

    @Override
    protected WorldConfiguration worldConfigInstance(final @NonNull ServerLevel level) {
        return level.plazmaConfig();
    }

    @Override
    protected ConfigurationOptions defaultOptions(final ConfigurationOptions options) {
        return options.serializers(builder -> builder
                .register(MapSerializer.TYPE, new MapSerializer(false))
                .register(new EnumValueSerializer())
                .register(new ComponentSerializer())
                .register(ResourceLocationSerializer.INSTANCE)
        );
    }

    @Override
    protected ConfigurationOptions defaultGlobalOptions(final ConfigurationOptions options, final RegistryAccess registryAccess) {
        return options;
    }

    @Override
    protected void setGlobalConfigInstance(final GlobalConfiguration instance) {
        GlobalConfiguration.set(instance);
    }

    @Override
    protected WorldConfiguration createWorldConfigInstance(final ContextMap contextMap) {
        return new WorldConfiguration(contextMap.require(WORLD_KEY));
    }

    @Override
    protected ConfigurationOptions defaultWorldOptions(final ConfigurationOptions options, final ContextMap contextMap) {
        return options;
    }

    @Override
    protected ContextMap createWorldContextMap(ServerLevel level) {
        return createWorldContextMap(level.levelStorageAccess.levelDirectory.path(), level.serverLevelData.getLevelName(), level.dimension().location(), level.registryAccess());
    }

    public static ContextMap createWorldContextMap(final Path dir, final String levelName, final ResourceLocation worldKey, final RegistryAccess registryAccess) {
        return ContextMap.builder()
                .put(WORLD_DIRECTORY, dir)
                .put(WORLD_NAME, levelName)
                .put(WORLD_KEY, worldKey)
                .put(REGISTRY_ACCESS, registryAccess)
                .build();
    }

    public static PlazmaConfigurations setup(final Path configDir) throws Exception {
        return new PlazmaConfigurations(configDir);
    }

}