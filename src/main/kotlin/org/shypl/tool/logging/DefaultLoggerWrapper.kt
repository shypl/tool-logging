package org.shypl.tool.logging

import org.slf4j.Logger

internal class DefaultLoggerWrapper(logger: Logger) : AbstractLoggerWrapper<Logger>(logger) {
	override fun error(message: String, error: Throwable?) {
		logger.error(message, error)
	}
	
	override fun warn(message: String, error: Throwable?) {
		logger.warn(message, error)
	}
	
	override fun info(message: String, error: Throwable?) {
		logger.info(message, error)
	}
	
	override fun debug(message: String, error: Throwable?) {
		logger.debug(message, error)
	}
	
	override fun trace(message: String, error: Throwable?) {
		logger.trace(message, error)
	}
	
	override fun log(level: Level, message: String, error: Throwable?) {
		when (level) {
			Level.ERROR -> error(message, error)
			Level.WARN  -> warn(message, error)
			Level.INFO  -> info(message, error)
			Level.DEBUG -> debug(message, error)
			Level.TRACE -> trace(message, error)
		}
	}
}