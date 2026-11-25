package org.plazmamc.plazma.configuration;

import io.leangen.geantyref.TypeToken;
import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.Configurations;
import io.papermc.paper.configuration.mapping.InnerClassFieldDiscoverer;
import io.papermc.paper.configuration.serializer.ComponentSerializer;
import io.papermc.paper.configuration.serializer.EnumValueSerializer;
import io.papermc.paper.configuration.serializer.IdentifierSerializer;
import io.papermc.paper.configuration.serializer.collection.TableSerializer;
import io.papermc.paper.configuration.serializer.collection.map.FastutilMapSerializer;
import io.papermc.paper.configuration.serializer.collection.map.MapSerializer;
import io.papermc.paper.configuration.serializer.registry.RegistryHolderSerializer;
import io.papermc.paper.configuration.serializer.registry.RegistryValueSerializer;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static io.leangen.geantyref.GenericTypeReflector.erase;

@NullMarked
public class PlazmaConfigurations extends Configurations<GlobalConfiguration, WorldConfiguration> {
    static final String GLOBAL_CONFIG_FILE_NAME = "plazmaConfigurations-global.yaml";
    static final String WORLD_CONFIG_FILE_NAME = "plazmaConfigurations-world.yaml";
    static final String WORLD_DEFAULTS_CONFIG_FILE_NAME = "plazmaConfigurations-world-defaults.yaml";

    private static final String GLOBAL_HEADER = String.format("""
            This is the global configuration file for Plazma.
            As you can see, there's a lot to configure. Some options may impact gameplay, so use
            with caution, and make sure you know what each option does before configuring.

            If you need help with the configuration or have any questions related to Plazma,
            join us in our Discord or check the docs page.

            The world configuration options have been moved inside
            their respective world folder. The files are named %s

            Website: https://plazmamc.org/
            Discord: https://plazmamc.org/discord""", WORLD_CONFIG_FILE_NAME);

    private static final String WORLD_DEFAULTS_HEADER = """
            This is the world defaults configuration file for Plazma.
            As you can see, there's a lot to configure. Some options may impact gameplay, so use
            with caution, and make sure you know what each option does before configuring.

            If you need help with the configuration or have any questions related to Plazma,
            join us in our Discord or check the docs page.

            Configuration options here apply to all worlds, unless you specify overrides inside
            the world-specific config file inside each world folder.

            Website: https://plazmamc.org/
            Discord: https://plazmamc.org/discord""";

    private static final Function<ContextMap, String> WORLD_HEADER = map -> String.format("""
        This is a per world configuration file for Plazma.
        This file may start empty but can be filled with settings to override ones in the config/%s

        World: %s""",
        WORLD_DEFAULTS_CONFIG_FILE_NAME,
        map.require(WORLD_KEY)
    );

    PlazmaConfigurations(final Path globalFolder) {
        super(globalFolder, GlobalConfiguration.class, WorldConfiguration.class, GLOBAL_CONFIG_FILE_NAME, WORLD_DEFAULTS_CONFIG_FILE_NAME, WORLD_CONFIG_FILE_NAME);
    }

    @Override
    protected YamlConfigurationLoader.Builder createLoaderBuilder() {
        return super.createLoaderBuilder()
            .defaultOptions(PlazmaConfigurations::defaultOptions);
    }

    private static ConfigurationOptions defaultOptions(final ConfigurationOptions options) {
        return options.serializers(builder -> builder
            .register(MapSerializer.TYPE, new MapSerializer(false))
            .register(new EnumValueSerializer())
            .register(new ComponentSerializer())
            .register(IdentifierSerializer.INSTANCE)
        );
    }

    @Override
    protected boolean isConfigType(final Type type) {
        return ConfigurationPart.class.isAssignableFrom(erase(type));
    }

    @Override
    protected int globalConfigVersion() {
        return GlobalConfiguration.CURRENT_VERSION;
    }

    @Override
    protected int worldConfigVersion() {
        return WorldConfiguration.CURRENT_VERSION;
    }

    @Override
    protected ObjectMapper.Factory.Builder createGlobalObjectMapperFactoryBuilder() {
        return defaultGlobalFactoryBuilder(super.createGlobalObjectMapperFactoryBuilder());
    }

    private static ObjectMapper.Factory.Builder defaultGlobalFactoryBuilder(
        final ObjectMapper.Factory.Builder builder
    ) {
        return builder
            .addDiscoverer(InnerClassFieldDiscoverer.create(Collections.emptyMap(), Collections.emptyList()));
    }

    @Override
    protected YamlConfigurationLoader.Builder createGlobalLoaderBuilder(final RegistryAccess registryAccess) {
        return super.createGlobalLoaderBuilder(registryAccess)
            .defaultOptions(options -> defaultGlobalOptions(registryAccess, options));
    }

    @SuppressWarnings("NullableProblems")
    private static ConfigurationOptions defaultGlobalOptions(
        final RegistryAccess registryAccess,
        final ConfigurationOptions options
    ) {
        return options
            .header(GLOBAL_HEADER)
            .serializers(builder -> builder
                .register(new RegistryValueSerializer<>(new TypeToken<>() {}, registryAccess, Registries.DATA_COMPONENT_TYPE, false))
            );
    }

    @Override
    public GlobalConfiguration initializeGlobalConfiguration(
        final RegistryAccess registryAccess
    ) throws ConfigurateException {
        final GlobalConfiguration configuration = super.initializeGlobalConfiguration(registryAccess);
        GlobalConfiguration.set(configuration);
        return configuration;
    }

    @Override
    protected ObjectMapper.Factory.Builder createWorldObjectMapperFactoryBuilder(final ContextMap contextMap) {
        return defaultWorldFactoryBuilder(contextMap, super.createWorldObjectMapperFactoryBuilder(contextMap));
    }

    private static ObjectMapper.Factory.Builder defaultWorldFactoryBuilder(
        final ContextMap contextMap,
        final ObjectMapper.Factory.Builder builder
    ) {
        return builder
            .addDiscoverer(InnerClassFieldDiscoverer.create(Map.of(WorldConfiguration.class, createWorldConfigurationInstance(contextMap)), Collections.emptyList()));
    }

    @Override
    protected YamlConfigurationLoader.Builder createWorldConfigLoaderBuilder(final ContextMap contextMap) {
        final RegistryAccess registryAccess = contextMap.require(REGISTRY_ACCESS);
        return super.createWorldConfigLoaderBuilder(contextMap)
            .defaultOptions(options -> defaultWorldOptions(registryAccess, contextMap, options));
    }

    @SuppressWarnings("NullableProblems")
    private static ConfigurationOptions defaultWorldOptions(
        final RegistryAccess registryAccess,
        final ContextMap contextMap,
        final ConfigurationOptions options
    ) {
        return options
            .header(contextMap.require(WORLD_KEY).equals(WORLD_DEFAULTS_KEY) ? WORLD_DEFAULTS_HEADER : WORLD_HEADER.apply(contextMap))
            .serializers(builder -> builder
                .register(new TypeToken<>() {}, new FastutilMapSerializer.SomethingToPrimitive<Reference2IntMap<?>>(Reference2IntOpenHashMap::new, Integer.TYPE))
                .register(new TypeToken<>() {}, new FastutilMapSerializer.SomethingToPrimitive<Reference2LongMap<?>>(Reference2LongOpenHashMap::new, Long.TYPE))
                .register(new TypeToken<>() {}, new FastutilMapSerializer.SomethingToSomething<Reference2ObjectMap<?, ?>>(Reference2ObjectOpenHashMap::new))
                .register(new TypeToken<>() {}, new TableSerializer())
                .register(new RegistryValueSerializer<>(new TypeToken<>() {}, registryAccess, Registries.ENTITY_TYPE, true))
                .register(new RegistryValueSerializer<>(Item.class, registryAccess, Registries.ITEM, true))
                .register(new RegistryValueSerializer<>(Block.class, registryAccess, Registries.BLOCK, true))
                .register(new RegistryHolderSerializer<>(new TypeToken<>() {}, registryAccess, Registries.FEATURE, false))
            );
    }

    @Contract("_ -> new")
    private static WorldConfiguration createWorldConfigurationInstance(final ContextMap contextMap) {
        return new WorldConfiguration(contextMap.require(WORLD_KEY));
    }

    @Override
    public WorldConfiguration createWorldConfig(final ContextMap contextMap) {
        try {
            return super.createWorldConfig(contextMap);
        } catch (final IOException exception) {
            final String levelKey = contextMap.require(WORLD_KEY).toString();
            throw new RuntimeException("Could not create world config for " + levelKey, exception);
        }
    }

    private static ContextMap createWorldContextMap(final ServerLevel level) {
        return createWorldContextMap(
            level.getServer().storageSource.getDimensionPath(level.dimension()),
            level.dimension().identifier(),
            level.registryAccess(),
            level.getGameRules()
        );
    }

    public static ContextMap createWorldContextMap(
        final Path dir,
        final Identifier worldKey,
        final RegistryAccess registryAccess,
        final GameRules gameRules
    ) {
        return ContextMap.builder()
            .put(WORLD_DIRECTORY, dir)
            .put(WORLD_KEY, worldKey)
            .put(REGISTRY_ACCESS, registryAccess)
            .put(GAME_RULES, gameRules)
            .build();
    }

    public void reloadConfigs(final MinecraftServer server) {
        try {
            this.initializeGlobalConfiguration(server.registryAccess(), reloader(this.globalConfigClass, GlobalConfiguration.get()));
            this.initializeWorldDefaultsConfiguration(server.registryAccess());
            for (final ServerLevel level : server.getAllLevels()) {
                this.createWorldConfig(createWorldContextMap(level), reloader(this.worldConfigClass, level.plazmaConfig()));
            }
        } catch (final Exception e) {
            throw new RuntimeException("Could not reload plazmaConfigurations configuration files", e);
        }
    }

    @Contract("_ -> new")
    public static PlazmaConfigurations setup(final Path configDir) {
        return new PlazmaConfigurations(configDir);
    }
}
