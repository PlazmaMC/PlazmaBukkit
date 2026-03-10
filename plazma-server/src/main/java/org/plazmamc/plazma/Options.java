package org.plazmamc.plazma;

import static java.lang.Boolean.getBoolean;
import static java.lang.Integer.getInteger;

public interface Options {

    boolean OPTIMIZE = !getBoolean("Plazma.disableConfigOptimization");
    int MAX_STACK_SIZE = getInteger("Plazma.maxStackSize", 99);

}