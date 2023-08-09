package org.shypl.tool.logging

interface Logger {
	val name: String
	
	val errorEnabled: Boolean
	val warnEnabled: Boolean
	val infoEnabled: Boolean
	val debugEnabled: Boolean
	val traceEnabled: Boolean
	
	fun isEnabled(level: Level): Boolean
	
	fun error(message: String, error: Throwable? = null)
	
	fun warn(message: String, error: Throwable? = null)
	
	fun info(message: String, error: Throwable? = null)
	
	fun debug(message: String, error: Throwable? = null)
	
	fun trace(message: String, error: Throwable? = null)
	
	fun log(level: Level, message: String, error: Throwable? = null)
}
