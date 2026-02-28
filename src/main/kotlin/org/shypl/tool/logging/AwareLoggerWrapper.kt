package org.shypl.tool.logging

import org.slf4j.spi.LocationAwareLogger

internal class AwareLoggerWrapper(logger: LocationAwareLogger) : AbstractLoggerWrapper<LocationAwareLogger>(logger) {
	private val fqcn = Logger::class.java.name
	
	override fun error(message: String, error: Throwable?) {
		log(LocationAwareLogger.ERROR_INT, message, error)
	}
	
	override fun warn(message: String, error: Throwable?) {
		log(LocationAwareLogger.WARN_INT, message, error)
	}
	
	override fun info(message: String, error: Throwable?) {
		log(LocationAwareLogger.INFO_INT, message, error)
	}
	
	override fun debug(message: String, error: Throwable?) {
		log(LocationAwareLogger.DEBUG_INT, message, error)
	}
	
	override fun trace(message: String, error: Throwable?) {
		log(LocationAwareLogger.TRACE_INT, message, error)
	}
	
	override fun log(level: Level, message: String, error: Throwable?) {
		log(level.laLevel, message, error)
	}
	
	private fun log(level: Int, message: String, t: Throwable? = null) {
		logger.log(null, fqcn, level, message, null, t)
	}
	
	private val Level.laLevel: Int
		get() = when (this) {
			Level.ERROR -> LocationAwareLogger.ERROR_INT
			Level.WARN  -> LocationAwareLogger.WARN_INT
			Level.INFO  -> LocationAwareLogger.INFO_INT
			Level.DEBUG -> LocationAwareLogger.DEBUG_INT
			Level.TRACE -> LocationAwareLogger.TRACE_INT
		}
	
}
