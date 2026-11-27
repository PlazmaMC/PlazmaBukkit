package org.plazmamc.plazma.simd;

import io.papermc.paper.annotation.DoNotUse;
import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

@Deprecated
@NullMarked
final class SIMDInternal {

    @Contract(" -> fail")
    @Deprecated(forRemoval = true)
    @DoNotUse
    private SIMDInternal() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @Deprecated
    static boolean canEnable(final Logger logger) throws NoClassDefFoundError {
        VectorSpecies<Integer> ISPEC = IntVector.SPECIES_PREFERRED;
        VectorSpecies<Float> FSPEC = FloatVector.SPECIES_PREFERRED;

        logger.info("Max SIMD vector size on this system is {} bits (int)", ISPEC.vectorBitSize());
        logger.info("Max SIMD vector size on this system is {} bits (float)", FSPEC.vectorBitSize());

        if (ISPEC.elementSize() < 2 || FSPEC.elementSize() < 2) {
            logger.warn("SIMD is not properly supported on this system!");
            return false;
        }

        return true;
    }
}
