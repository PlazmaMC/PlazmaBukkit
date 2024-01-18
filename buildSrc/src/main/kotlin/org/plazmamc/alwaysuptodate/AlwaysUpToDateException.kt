package org.plazmamc.alwaysuptodate

class AlwaysUpToDateException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
