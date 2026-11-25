package org.plazmamc.plazma;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.plazmamc.plazma.configuration.GlobalConfiguration;

@NullMarked
public interface Options {

    @Contract(pure = true)
    static GlobalConfiguration global() {
        return GlobalConfiguration.get();
    }

}
