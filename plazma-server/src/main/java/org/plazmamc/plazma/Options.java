package org.plazmamc.plazma;

import static java.lang.Boolean.getBoolean;

public interface Options {

    boolean ASYNC = getBoolean("Plazma.forciblyAllowAsyncAccess");
    boolean OPTIMIZE = !getBoolean("Plazma.disableConfigOptimization");
    boolean NO_WARN = getBoolean("Plazma.iKnowWhatIAmDoing");
    boolean NO_WARN_DEV = getBoolean("Plazma.iKnowWhatIAmDoingISwear");

}
