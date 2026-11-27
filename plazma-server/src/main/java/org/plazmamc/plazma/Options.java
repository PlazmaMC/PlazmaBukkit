package org.plazmamc.plazma;

import io.papermc.paper.annotation.DoNotUse;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.plazmamc.plazma.configuration.GlobalConfiguration;

import static java.lang.Boolean.getBoolean;

@NullMarked
public final class Options {

    @Contract(" -> fail")
    @Deprecated(forRemoval = true)
    @DoNotUse
    private Options() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @Contract(pure = true)
    public static GlobalConfiguration global() {
        return GlobalConfiguration.get();
    }

    public static final boolean ENABLE_SIMD = getBoolean("plazma.enableSIMD");

}
