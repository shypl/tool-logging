package org.shypl.tool.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LoggerWrapperTest {
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
	fun testDefaultLoggerWrapperEquality() {
		val slf4jLogger = LoggerFactory.getLogger("test.wrapper equality")
		val wrapper1 = DefaultLoggerWrapper(slf4jLogger)
		val wrapper2 = DefaultLoggerWrapper(slf4jLogger)
		val wrapperOther = DefaultLoggerWrapper(LoggerFactory.getLogger("other"))

		assertEquals(wrapper1, wrapper2)
		assertEquals(wrapper1.hashCode(), wrapper2.hashCode())
		assertEquals(wrapper1 == wrapperOther, false)
	}

	@Test
	fun testDefaultLoggerWrapperName() {
		val slf4jLogger = LoggerFactory.getLogger("test.wrapper.name")
		val wrapper = DefaultLoggerWrapper(slf4jLogger)

		assertEquals("test.wrapper.name", wrapper.name)
	}

	@Test
	fun testDefaultLoggerWrapperLevelChecks() {
		val logger = loggerContext.getLogger("test.wrapper.levels")
		logger.level = Level.TRACE
		val wrapper = DefaultLoggerWrapper(logger)

		assertEquals(wrapper.errorEnabled, true)
		assertEquals(wrapper.warnEnabled, true)
		assertEquals(wrapper.infoEnabled, true)
		assertEquals(wrapper.debugEnabled, true)
		assertEquals(wrapper.traceEnabled, true)

		logger.level = Level.ERROR
		assertEquals(wrapper.errorEnabled, true)
		assertEquals(wrapper.warnEnabled, false)
		assertEquals(wrapper.infoEnabled, false)
		assertEquals(wrapper.debugEnabled, false)
		assertEquals(wrapper.traceEnabled, false)
	}

	@Test
	fun testDefaultLoggerWrapperIsEnabled() {
		val logger = loggerContext.getLogger("test.wrapper.isEnabled")
		logger.level = Level.DEBUG
		val wrapper = DefaultLoggerWrapper(logger)

		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.ERROR), true)
		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.WARN), true)
		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.INFO), true)
		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.DEBUG), true)
		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.TRACE), false)
	}

	@Test
	fun testDefaultLoggerWrapperLogging() {
		val logger = loggerContext.getLogger("test.wrapper.logging")
		logger.level = Level.TRACE
		val wrapper = DefaultLoggerWrapper(logger)

		wrapper.error("error msg")
		wrapper.warn("warn msg")
		wrapper.info("info msg")
		wrapper.debug("debug msg")
		wrapper.trace("trace msg")

		assertEquals(5, listAppender.list.size)
		assertEquals("error msg", listAppender.list[0].message)
		assertEquals(Level.ERROR, listAppender.list[0].level)
		assertEquals("warn msg", listAppender.list[1].message)
		assertEquals(Level.WARN, listAppender.list[1].level)
		assertEquals("info msg", listAppender.list[2].message)
		assertEquals(Level.INFO, listAppender.list[2].level)
		assertEquals("debug msg", listAppender.list[3].message)
		assertEquals(Level.DEBUG, listAppender.list[3].level)
		assertEquals("trace msg", listAppender.list[4].message)
		assertEquals(Level.TRACE, listAppender.list[4].level)
	}

	@Test
	fun testDefaultLoggerWrapperLogMethod() {
		val logger = loggerContext.getLogger("test.wrapper.log")
		logger.level = Level.TRACE
		val wrapper = DefaultLoggerWrapper(logger)

		wrapper.log(org.shypl.tool.logging.Level.ERROR, "log error")
		wrapper.log(org.shypl.tool.logging.Level.WARN, "log warn")
		wrapper.log(org.shypl.tool.logging.Level.INFO, "log info")
		wrapper.log(org.shypl.tool.logging.Level.DEBUG, "log debug")
		wrapper.log(org.shypl.tool.logging.Level.TRACE, "log trace")

		assertEquals(5, listAppender.list.size)
		assertEquals("log error", listAppender.list[0].message)
		assertEquals(Level.ERROR, listAppender.list[0].level)
		assertEquals("log warn", listAppender.list[1].message)
		assertEquals(Level.WARN, listAppender.list[1].level)
		assertEquals("log info", listAppender.list[2].message)
		assertEquals(Level.INFO, listAppender.list[2].level)
		assertEquals("log debug", listAppender.list[3].message)
		assertEquals(Level.DEBUG, listAppender.list[3].level)
		assertEquals("log trace", listAppender.list[4].message)
		assertEquals(Level.TRACE, listAppender.list[4].level)
	}
}
