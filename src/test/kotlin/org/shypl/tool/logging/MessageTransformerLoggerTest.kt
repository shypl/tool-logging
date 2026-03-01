package org.shypl.tool.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageTransformerLoggerTest {
	private val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
	private val rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
	private val listAppender = ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>()

	@BeforeTest
	fun setup() {
		rootLogger.level = Level.TRACE
		listAppender.start()
		rootLogger.addAppender(listAppender)
	}

	@AfterTest
	fun tearDown() {
		rootLogger.detachAppender(listAppender)
		listAppender.list.clear()
		rootLogger.level = Level.WARN
	}

	@Test
	fun testMessageTransformerLoggerName() {
		val logger = Logging.getLogger("test.transform.name")
		val transformer = object : MessageTransformerLogger(logger) {
			override fun transform(message: String) = "transformed: $message"
		}

		assertEquals("test.transform.name", transformer.name)
	}

	@Test
	fun testMessageTransformerLoggerLevelChecks() {
		val logger = Logging.getLogger("test.transform.levels")
		val transformer = object : MessageTransformerLogger(logger) {
			override fun transform(message: String) = "transformed: $message"
		}

		assertEquals(transformer.errorEnabled, true)
		assertEquals(transformer.warnEnabled, true)
		assertEquals(transformer.infoEnabled, true)
		assertEquals(transformer.debugEnabled, true)
		assertEquals(transformer.traceEnabled, true)
	}

	@Test
	fun testMessageTransformerLoggerTransformsMessages() {
		val logger = Logging.getLogger("test.transform.messages")
		val transformer = object : MessageTransformerLogger(logger) {
			override fun transform(message: String) = "PREFIX: $message"
		}

		transformer.info("test message")

		assertEquals(1, listAppender.list.size)
		assertEquals("PREFIX: test message", listAppender.list[0].message)
	}

	@Test
	fun testMessageTransformerLoggerTransformsAllLevels() {
		val logger = Logging.getLogger("test.transform.alllevels")
		val transformer = object : MessageTransformerLogger(logger) {
			override fun transform(message: String) = "[TRANSFORMED] $message"
		}

		transformer.error("error")
		transformer.warn("warn")
		transformer.info("info")
		transformer.debug("debug")
		transformer.trace("trace")

		assertEquals(5, listAppender.list.size)
		assertEquals("[TRANSFORMED] error", listAppender.list[0].message)
		assertEquals("[TRANSFORMED] warn", listAppender.list[1].message)
		assertEquals("[TRANSFORMED] info", listAppender.list[2].message)
		assertEquals("[TRANSFORMED] debug", listAppender.list[3].message)
		assertEquals("[TRANSFORMED] trace", listAppender.list[4].message)
	}

	@Test
	fun testMessageTransformerLoggerWithThrowable() {
		val logger = Logging.getLogger("test.transform.throwable")
		val transformer = object : MessageTransformerLogger(logger) {
			override fun transform(message: String) = "Error: $message"
		}
		val exception = RuntimeException("test exception")

		transformer.error("error with throwable", exception)

		assertEquals(1, listAppender.list.size)
		assertEquals("Error: error with throwable", listAppender.list[0].message)
		assertEquals("test exception", listAppender.list[0].throwableProxy.message)
	}

	@Test
	fun testMessageTransformerLoggerRespectsLevel() {
		val logger = Logging.getLogger("test.transform.respect")
		loggerContext.getLogger("test.transform.respect").level = Level.INFO
		val transformer = object : MessageTransformerLogger(logger) {
			override fun transform(message: String) = "TRANSFORMED: $message"
		}

		transformer.info("info message")
		transformer.debug("debug message")

		assertEquals(1, listAppender.list.size)
		assertEquals("TRANSFORMED: info message", listAppender.list[0].message)
	}

	@Test
	fun testMessageTransformerLoggerLogMethod() {
		val logger = Logging.getLogger("test.transform.log")
		val transformer = object : MessageTransformerLogger(logger) {
			override fun transform(message: String) = "LOG: $message"
		}

		transformer.log(org.shypl.tool.logging.Level.ERROR, "error via log")
		transformer.log(org.shypl.tool.logging.Level.WARN, "warn via log")
		transformer.log(org.shypl.tool.logging.Level.INFO, "info via log")

		assertEquals(3, listAppender.list.size)
		assertEquals("LOG: error via log", listAppender.list[0].message)
		assertEquals("LOG: warn via log", listAppender.list[1].message)
		assertEquals("LOG: info via log", listAppender.list[2].message)
	}
}
