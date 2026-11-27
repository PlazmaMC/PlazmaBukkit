package org.plazmamc.plazma.simd;

import io.papermc.paper.annotation.DoNotUse;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Deprecated
@NullMarked
public final class SIMD {

    @Contract(" -> fail")
    @Deprecated(forRemoval = true)
    @DoNotUse
    private SIMD() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static @Nullable Boolean supported = null;
    private static boolean enabled = false;

    @Deprecated
    public static void enable() {
        if (enabled) {
            throw new IllegalStateException("SIMD is already enabled!");
        }
        if (supported == null) {
            throw new IllegalStateException("SIMD support has not been checked yet");
        }
        if (!supported) {
            throw new IllegalStateException("SIMD is not supported on this system!");
        }

        enabled = true;
    }

    @Contract(pure = true)
    @Deprecated
    public static boolean isEnabled() {
        return enabled;
    }

    @Deprecated
    public static boolean canEnable(final Logger logger) {
        if (supported == null) {
            try {
                supported = SIMDInternal.canEnable(logger);
            } catch (final NoClassDefFoundError | Exception e) {
                supported = false;
            }
        }

        return supported;
    }

}
