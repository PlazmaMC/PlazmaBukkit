package org.plazmamc.alwaysuptodate.utils

fun <A, B, C> Pair<Pair<A, B>, C>.flatten() = Triple(first.first, first.second, second)
