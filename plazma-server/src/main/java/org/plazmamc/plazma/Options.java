package org.plazmamc.plazma;

import static java.lang.Boolean.getBoolean;

public interface Options {

    boolean OPTIMIZE = !getBoolean("Plazma.disableConfigOptimization");

}
