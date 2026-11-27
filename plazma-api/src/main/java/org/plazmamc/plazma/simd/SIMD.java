package org.plazmamc.plazma.simd;

import io.papermc.paper.annotation.DoNotUse;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
@NullMarked
public final class SIMD {

    @Contract(" -> fail")
    @Deprecated(forRemoval = true)
    @DoNotUse
    private SIMD() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SIMD.class);
    private static @Nullable Boolean supported = null;
    private static @Nullable Boolean enabled = null;

    /**
     * @hidden
     */
    @ApiStatus.Internal
    @Deprecated
    public static void initialize(final boolean enable) {
        if (enabled != null) {
            throw new IllegalStateException("enable() can only be called once");
        }

        if (supported == null) {
            try {
                supported = SIMDInternal.canEnable(LOGGER);
            } catch (final NoClassDefFoundError | Exception e) {
                supported = false;
            }
        }

        if (supported) {
            if (enable) {
                enabled = true;
                LOGGER.info("SIMD operations detected as functional. Will replace some operations with faster versions.");
                return;
            }

            LOGGER.info("SIMD operations are available for your server.");
            LOGGER.info("To enable additional optimizations, add \"-Dplazma.enableSIMD\" to your startup flags.");
        } else if (enable) {
            LOGGER.warn("SIMD operations are available for your server, but are not configured!");
            LOGGER.warn("To enable additional optimizations, add \"--add-modules=jdk.incubator.vector\" to your startup flags, BEFORE the \"-jar\".");
            LOGGER.warn("If you have already added this flag, then SIMD operations are not supported on your JVM or CPU.");
        }
        enabled = false;
    }

    @Contract(pure = true)
    @Deprecated
    public static boolean isEnabled() {
        assert enabled != null;
        return enabled;
    }

}
