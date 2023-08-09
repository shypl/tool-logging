package org.shypl.tool.logging

abstract class MessageTransformerLogger(private val target: Logger) : Logger by target {
	override fun error(message: String, error: Throwable?) {
		log(Level.ERROR, message, error)
	}
	
	override fun warn(message: String, error: Throwable?) {
		log(Level.WARN, message, error)
	}
	
	override fun info(message: String, error: Throwable?) {
		log(Level.INFO, message, error)
	}
	
	override fun debug(message: String, error: Throwable?) {
		log(Level.DEBUG, message, error)
	}
	
	override fun trace(message: String, error: Throwable?) {
		log(Level.TRACE, message, error)
	}
	
	override fun log(level: Level, message: String, error: Throwable?) {
		if (isEnabled(level)) {
			target.log(level, transform(message), error)
		}
	}
	
	protected abstract fun transform(message: String): String
}
