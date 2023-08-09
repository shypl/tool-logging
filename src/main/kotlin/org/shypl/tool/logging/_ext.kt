package org.shypl.tool.logging

inline fun <reified T : Any> Logging.getLogger(): Logger {
	return getLogger(T::class)
}

inline val Any.ownLogger: Logger
	get() = Logging.getLogger(this::class)

inline fun Logger.error(message: () -> String) {
	if (errorEnabled) error(message())
}

inline fun Logger.error(t: Throwable, message: () -> String) {
	if (errorEnabled) error(message(), t)
}


inline fun Logger.warn(message: () -> String) {
	if (warnEnabled) warn(message())
}

inline fun Logger.warn(t: Throwable, message: () -> String) {
	if (warnEnabled) warn(message(), t)
}


inline fun Logger.info(message: () -> String) {
	if (infoEnabled) info(message())
}

inline fun Logger.info(t: Throwable, message: () -> String) {
	if (infoEnabled) info(message(), t)
}


inline fun Logger.debug(message: () -> String) {
	if (debugEnabled) debug(message())
}

inline fun Logger.debug(t: Throwable, message: () -> String) {
	if (debugEnabled) debug(message(), t)
}


inline fun Logger.trace(message: () -> String) {
	if (traceEnabled) trace(message())
}

inline fun Logger.trace(t: Throwable, message: () -> String) {
	if (traceEnabled) trace(message(), t)
}

inline fun Logger.wrap(crossinline transformer: (String) -> String): Logger {
	return object : MessageTransformerLogger(this) {
		override fun transform(message: String) = transformer(message)
	}
}

fun Logger.wrap(prefix: String): Logger {
	return object : MessageTransformerLogger(this) {
		override fun transform(message: String) = prefix + message
	}
}

inline fun Logger.wrap(phrase: String, crossinline transformer: (phrase: String, message: String) -> String): Logger {
	return object : MessageTransformerLogger(this) {
		override fun transform(message: String) = transformer(phrase, message)
	}
}