package org.plazmamc.plazma.configuration;

import com.google.common.collect.Table;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.configuration.Configurations;
import io.papermc.paper.configuration.Metadata;
import io.papermc.paper.configuration.serializer.ComponentSerializer;
import io.papermc.paper.configuration.serializer.EnumValueSerializer;
import io.papermc.paper.configuration.serializer.ResourceLocationSerializer;
import io.papermc.paper.configuration.serializer.StringRepresentableSerializer;
import io.papermc.paper.configuration.serializer.collection.TableSerializer;
import io.papermc.paper.configuration.serializer.collection.map.FastutilMapSerializer;
import io.papermc.paper.configuration.serializer.collection.map.MapSerializer;
import io.papermc.paper.configuration.serializer.registry.RegistryHolderSerializer;
import io.papermc.paper.configuration.serializer.registry.RegistryValueSerializer;
import io.papermc.paper.configuration.type.DespawnRange;
import io.papermc.paper.configuration.type.EngineMode;
import io.papermc.paper.configuration.type.fallback.FallbackValueSerializer;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
        final RegistryAccess access = contextMap.require(REGISTRY_ACCESS);
        return options.serializers(serializers -> serializers
                .register(new TypeToken<Reference2IntMap<?>>() {
                }, new FastutilMapSerializer.SomethingToPrimitive<Reference2IntMap<?>>(Reference2IntOpenHashMap::new, Integer.TYPE))
                .register(new TypeToken<Reference2LongMap<?>>() {
                }, new FastutilMapSerializer.SomethingToPrimitive<Reference2LongMap<?>>(Reference2LongOpenHashMap::new, Long.TYPE))
                .register(new TypeToken<Reference2BooleanMap<?>>() {
                }, new FastutilMapSerializer.SomethingToPrimitive<Reference2BooleanMap<?>>(Reference2BooleanOpenHashMap::new, Boolean.TYPE))
                .register(new TypeToken<Reference2ObjectMap<?, ?>>() {
                }, new FastutilMapSerializer.SomethingToSomething<Reference2ObjectMap<?, ?>>(Reference2ObjectOpenHashMap::new))
                .register(new TypeToken<Table<?, ?, ?>>() {
                }, new TableSerializer())
                .register(new RegistryValueSerializer<>(new TypeToken<EntityType<?>>() {
                }, access, Registries.ENTITY_TYPE, true))
                .register(new RegistryValueSerializer<>(Item.class, access, Registries.ITEM, true))
                .register(new RegistryValueSerializer<>(Block.class, access, Registries.BLOCK, true))
        );
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