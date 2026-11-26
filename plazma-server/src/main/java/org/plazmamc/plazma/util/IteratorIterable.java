package org.plazmamc.plazma.util;

import org.jspecify.annotations.NullMarked;

import java.util.Iterator;

@NullMarked
public record IteratorIterable<T>(Iterator<T> iterator) implements Iterable<T> {}
