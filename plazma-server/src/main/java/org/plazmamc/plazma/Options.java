package org.plazmamc.plazma;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.plazmamc.plazma.configurations.GlobalConfiguration;

import static java.lang.Boolean.getBoolean;

public interface Options {

    @Contract(pure = true)
    static @NotNull GlobalConfiguration global() {
        return GlobalConfiguration.get();
    }

    boolean ASYNC = getBoolean("Plazma.forciblyAllowAsyncAccess");
    boolean OPTIMIZE = !getBoolean("Plazma.disableConfigOptimization");
    boolean NO_WARN = getBoolean("Plazma.iKnowWhatIAmDoing");
    boolean NO_WARN_DEV = getBoolean("Plazma.iKnowWhatIAmDoingISwear");

}
