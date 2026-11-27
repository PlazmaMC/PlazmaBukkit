package org.plazmamc.plazma.simd;

import com.mojang.logging.LogUtils;
import io.papermc.paper.annotation.DoNotUse;
import org.jetbrains.annotations.Contract;
import org.plazmamc.plazma.Options;
import org.slf4j.Logger;

@Deprecated
public final class SIMDProvider {

    @Contract(" -> fail")
    @Deprecated
    @DoNotUse
    private SIMDProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void initialize() {
        final boolean supported = SIMD.canEnable(LOGGER);
        if (Options.ENABLE_SIMD) {
            if (supported) {
                SIMD.enable();
                LOGGER.info("SIMD operations detected as functional. Will replace some operations with faster versions.");
            } else {
                LOGGER.warn("SIMD operations are available for your server, but are not configured!");
                LOGGER.warn("To enable additional optimizations, add \"--add-modules=jdk.incubator.vector\" to your startup flags, BEFORE the \"-jar\".");
                LOGGER.warn("If you have already added this flag, then SIMD operations are not supported on your JVM or CPU.");
            }
        } else if (supported) {
            LOGGER.info("SIMD operations are available for your server.");
            LOGGER.info("To enable additional optimizations, add \"-Dplazma.enableSIMD\" to your startup flags.");
        }
    }

}
