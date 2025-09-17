/*
 * Project: Pufferfish (https://github.com/pufferfish-gg/Pufferfish)
 * Author: Kevin Raneri <kevin.raneri@gmail.com>
 * License: GPL-3.0 (https://www.gnu.org/licenses/gpl-3.0.html)
 *
 * Project: Leaf (https://github.com/Winds-Studio/Leaf)
 * License: MIT (https://opensource.org/license/MIT)
 */
package org.plazmamc.plazma.util;

import java.util.Iterator;

import org.jetbrains.annotations.NotNull;

public record IterableWrapper<T>(Iterator<T> iterator) implements Iterable<T> {
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return iterator;
    }
}