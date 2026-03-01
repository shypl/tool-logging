package org.shypl.tool.logging

import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExtensionAndWrapCoverageTest {
	@Test
	fun reflectiveInlineLoggerAccessorsWork() {
		val extClass = Class.forName("org.shypl.tool.logging._extKt")
		
		val getLoggerMethod = extClass.getMethod("getLogger", Logging::class.java)
		val reifiedCallError = assertFailsWith<java.lang.reflect.InvocationTargetException> {
			getLoggerMethod.invoke(null, Logging)
		}
		assertTrue(reifiedCallError.targetException is UnsupportedOperationException)
		
		val getOwnLoggerMethod = extClass.getMethod("getOwnLogger", Any::class.java)
		val ownLogger = getOwnLoggerMethod.invoke(null, StubClass()) as Logger
		assertEquals(Logging.getLogger(StubClass::class), ownLogger)
	}
	
	@Test
	fun reflectiveLazyExtensionsRespectEnabledFlags() {
		val extClass = Class.forName("org.shypl.tool.logging._extKt")
		val logger = CaptureLogger()
		val noThrowableMethods = listOf(
			Level.ERROR to extClass.getMethod("error", Logger::class.java, Function0::class.java),
			Level.WARN to extClass.getMethod("warn", Logger::class.java, Function0::class.java),
			Level.INFO to extClass.getMethod("info", Logger::class.java, Function0::class.java),
			Level.DEBUG to extClass.getMethod("debug", Logger::class.java, Function0::class.java),
			Level.TRACE to extClass.getMethod("trace", Logger::class.java, Function0::class.java)
		)
		val throwableMethods = listOf(
			Level.ERROR to extClass.getMethod("error", Logger::class.java, Throwable::class.java, Function0::class.java),
			Level.WARN to extClass.getMethod("warn", Logger::class.java, Throwable::class.java, Function0::class.java),
			Level.INFO to extClass.getMethod("info", Logger::class.java, Throwable::class.java, Function0::class.java),
			Level.DEBUG to extClass.getMethod("debug", Logger::class.java, Throwable::class.java, Function0::class.java),
			Level.TRACE to extClass.getMethod("trace", Logger::class.java, Throwable::class.java, Function0::class.java)
		)
		
		logger.setAllEnabled(false)
		var lazyCalls = 0
		
		for ((level, method) in noThrowableMethods) {
			method.invoke(null, logger, { lazyCalls++; "lazy-$level" })
		}
		for ((level, method) in throwableMethods) {
			method.invoke(null, logger, RuntimeException("disabled-$level"), { lazyCalls++; "lazy-$level-with-throwable" })
		}
		
		assertEquals(0, lazyCalls)
		assertEquals(0, logger.calls.size)
		
		logger.setAllEnabled(true)
		
		for ((level, method) in noThrowableMethods) {
			method.invoke(null, logger, { lazyCalls++; "lazy-$level" })
		}
		for ((level, method) in throwableMethods) {
			method.invoke(null, logger, RuntimeException("enabled-$level"), { lazyCalls++; "lazy-$level-with-throwable" })
		}
		
		assertEquals(10, lazyCalls)
		assertEquals(10, logger.calls.size)
		assertEquals(
			listOf(
				"lazy-ERROR",
				"lazy-WARN",
				"lazy-INFO",
				"lazy-DEBUG",
				"lazy-TRACE",
				"lazy-ERROR-with-throwable",
				"lazy-WARN-with-throwable",
				"lazy-INFO-with-throwable",
				"lazy-DEBUG-with-throwable",
				"lazy-TRACE-with-throwable"
			),
			logger.calls.map { it.message }
		)
	}
	
	@Test
	fun reflectiveWrapExtensionsTransformMessages() {
		val extClass = Class.forName("org.shypl.tool.logging._extKt")
		val logger = CaptureLogger()
		logger.setAllEnabled(true)
		
		val wrapWithTransformerMethod = extClass.getMethod(
			"wrap",
			Logger::class.java,
			Function1::class.java
		)
		val wrapWithPhraseMethod = extClass.getMethod(
			"wrap",
			Logger::class.java,
			String::class.java,
			Function2::class.java
		)
		
		val wrappedWithTransformer = wrapWithTransformerMethod.invoke(null, logger, { message: String -> "[$message]" }) as Logger
		val wrappedWithPhrase = wrapWithPhraseMethod.invoke(
			null,
			logger,
			"CTX",
			{ phrase: String, message: String -> "$phrase::$message" }
		) as Logger
		
		wrappedWithTransformer.info("first")
		wrappedWithPhrase.info("second")
		
		assertEquals(2, logger.calls.size)
		assertEquals("[first]", logger.calls[0].message)
		assertEquals("CTX::second", logger.calls[1].message)
	}
	
	@Test
	fun privateLoggingWrapUsesDefaultWrapperForNonAwareLogger() {
		val wrapMethod = Logging::class.java.getDeclaredMethod("wrap", org.slf4j.Logger::class.java)
		wrapMethod.isAccessible = true
		
		val slf4jLogger = Proxy.newProxyInstance(
			org.slf4j.Logger::class.java.classLoader,
			arrayOf(org.slf4j.Logger::class.java)
		) { _, method, _ ->
			when (method.name) {
				"getName" -> "proxy.logger"
				"equals" -> false
				"hashCode" -> 0
				"toString" -> "proxy.logger"
				else -> defaultValue(method.returnType)
			}
		} as org.slf4j.Logger
		
		val wrapped = wrapMethod.invoke(null, slf4jLogger) as Logger
		
		assertTrue(wrapped is DefaultLoggerWrapper)
		assertEquals("proxy.logger", wrapped.name)
	}
	
	private fun defaultValue(type: Class<*>): Any? = when (type) {
		java.lang.Boolean.TYPE -> false
		java.lang.Byte.TYPE -> 0.toByte()
		java.lang.Short.TYPE -> 0.toShort()
		Integer.TYPE         -> 0
		java.lang.Long.TYPE  -> 0L
		java.lang.Float.TYPE -> 0f
		java.lang.Double.TYPE -> 0.0
		Character.TYPE        -> 0.toChar()
		else                  -> null
	}
	
	private data class LoggedCall(
		val level: Level,
		val message: String,
		val error: Throwable?
	)
	
	private class CaptureLogger : Logger {
		override val name: String = "capture"
		
		override var errorEnabled: Boolean = true
		override var warnEnabled: Boolean = true
		override var infoEnabled: Boolean = true
		override var debugEnabled: Boolean = true
		override var traceEnabled: Boolean = true
		
		val calls = mutableListOf<LoggedCall>()
		
		fun setAllEnabled(enabled: Boolean) {
			errorEnabled = enabled
			warnEnabled = enabled
			infoEnabled = enabled
			debugEnabled = enabled
			traceEnabled = enabled
		}
		
		override fun isEnabled(level: Level): Boolean = when (level) {
			Level.ERROR -> errorEnabled
			Level.WARN -> warnEnabled
			Level.INFO -> infoEnabled
			Level.DEBUG -> debugEnabled
			Level.TRACE -> traceEnabled
		}
		
		override fun error(message: String, error: Throwable?) {
			calls += LoggedCall(Level.ERROR, message, error)
		}
		
		override fun warn(message: String, error: Throwable?) {
			calls += LoggedCall(Level.WARN, message, error)
		}
		
		override fun info(message: String, error: Throwable?) {
			calls += LoggedCall(Level.INFO, message, error)
		}
		
		override fun debug(message: String, error: Throwable?) {
			calls += LoggedCall(Level.DEBUG, message, error)
		}
		
		override fun trace(message: String, error: Throwable?) {
			calls += LoggedCall(Level.TRACE, message, error)
		}
		
		override fun log(level: Level, message: String, error: Throwable?) {
			calls += LoggedCall(level, message, error)
		}
	}
}
