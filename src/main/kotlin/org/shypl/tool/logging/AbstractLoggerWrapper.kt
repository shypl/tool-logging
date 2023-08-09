package org.shypl.tool.logging

import org.slf4j.Logger as Slf4jLogger

internal abstract class AbstractLoggerWrapper<L : Slf4jLogger>(protected val logger: L) : Logger {
	
	override val name: String
		get() = logger.name
	
	override val errorEnabled: Boolean
		get() = logger.isErrorEnabled
	
	override val warnEnabled: Boolean
		get() = logger.isWarnEnabled
	
	override val infoEnabled: Boolean
		get() = logger.isInfoEnabled
	
	override val debugEnabled: Boolean
		get() = logger.isDebugEnabled
	
	override val traceEnabled: Boolean
		get() = logger.isTraceEnabled
	
	override fun isEnabled(level: Level): Boolean {
		return when (level) {
			Level.ERROR -> errorEnabled
			Level.WARN  -> warnEnabled
			Level.INFO  -> infoEnabled
			Level.DEBUG -> debugEnabled
			Level.TRACE -> traceEnabled
		}
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as AbstractLoggerWrapper<*>
		
		return logger == other.logger
	}
	
	override fun hashCode(): Int {
		return logger.hashCode()
	}
}
