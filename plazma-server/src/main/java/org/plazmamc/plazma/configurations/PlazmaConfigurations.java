package org.plazmamc.plazma.configurations;

import io.papermc.paper.configuration.Configurations;
import io.papermc.paper.configuration.serializer.ComponentSerializer;
import io.papermc.paper.configuration.serializer.EnumValueSerializer;
import io.papermc.paper.configuration.serializer.ResourceLocationSerializer;
import io.papermc.paper.configuration.serializer.collections.MapSerializer;
import joptsimple.OptionSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationOptions;
import java.io.File;
import java.nio.file.Path;

@DefaultQualifier(NonNull.class)
public final class PlazmaConfigurations extends Configurations<GlobalConfiguration, WorldConfiguration> {

    private PlazmaConfigurations(final Path configDir) {
        super(
            configDir,

            GlobalConfiguration.class,
            // TODO: yaml (convert)
            "plazma-global.yml",
            """
                This is the global configuration file for Plazma.
               
                As you can see, there's a lot to configure. Some options may
                impact gameplay, so use with caution, and make sure you know
                what each option does before configuring.
    
                A description of the options can be found on the official docs,
                If you need help with the configuration or have any questions
                related to Plazma, join us in our Discord for Plazma, or create
                discussions on our GitHub repository.
                
                World configurations can be set in the plazma-world-defaults.yml
                
                Docs: https://docs.plazmamc.org/
                GitHub: https://github.com/PlazmaMC/Plazma
                Support: https://plazmamc.org/discord
                """,
            GlobalConfiguration.VERSION,

            WorldConfiguration.class,
            // TODO: plazma-world-defaults.yml -> plazma-world.yaml (convert)
            "plazma-world-defaults.yml",
            """
                This is the default world configuration file for Plazma.
               
                As you can see, there's a lot to configure. Some options may
                impact gameplay, so use with caution, and make sure you know
                what each option does before configuring.
    
                A description of the options can be found on the official docs,
                If you need help with the configuration or have any questions
                related to Plazma, join us in our Discord for Plazma, or create
                discussions on our GitHub repository.
                
                World-specific settings can be set in the plazma-world.yml file
                within each world folder, and the same settings apply to all
                worlds unless they are overwritten through the world-specific
                settings file.
                
                Docs: https://docs.plazmamc.org/
                GitHub: https://github.com/PlazmaMC/Plazma
                Support: https://plazmamc.org/discord
                """,
            // TODO: yaml (convert)
            "plazma-world.yml",
            (map) -> String.format("""
                This is world-specific Plazma configuration file for the world %s (%s).

                This file may start empty, but can be filled with options to
                override world default configuration. Some options may impact
                gameplay, so use with caution, and make sure you know what each
                option does before configuring.
    
                A description of the options can be found on the official docs,
                If you need help with the configuration or have any questions
                related to Plazma, join us in our Discord for Plazma, or create
                discussions on our GitHub repository.
                
                Docs: https://docs.plazmamc.org/
                GitHub: https://github.com/PlazmaMC/Plazma
                Support: https://plazmamc.org/discord
                """, map.require(WORLD_NAME), map.require(WORLD_KEY)),
            0
        );
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
        if (GlobalConfiguration.INSTANCE != null) {
            throw new IllegalStateException("Global configuration is already initialized");
        }
        GlobalConfiguration.INSTANCE = instance;
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
    protected ContextMap createWorldContextMap(final ServerLevel level) {
        return ContextMap.builder()
            .put(WORLD_DIRECTORY, level.levelStorageAccess.levelDirectory.path())
            .put(WORLD_NAME, level.serverLevelData.getLevelName())
            .put(WORLD_KEY, level.dimension().location())
            .build();
    }

    public static PlazmaConfigurations create(final OptionSet optionSet) {
        return new PlazmaConfigurations(((File) optionSet.valueOf("paper-settings-directory")).toPath());
    }

}
