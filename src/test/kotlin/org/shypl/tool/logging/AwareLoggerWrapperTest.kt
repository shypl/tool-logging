package org.shypl.tool.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory
import org.slf4j.spi.LocationAwareLogger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AwareLoggerWrapperTest {
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
	fun testAwareLoggerWrapperIsLocationAware() {
		val slf4jLogger = LoggerFactory.getLogger("test.aware.check")
		assertEquals(slf4jLogger is LocationAwareLogger, true)
	}

	@Test
	fun testAwareLoggerWrapperName() {
		val slf4jLogger = LoggerFactory.getLogger("test.aware.name")
		val wrapper = AwareLoggerWrapper(slf4jLogger as LocationAwareLogger)

		assertEquals("test.aware.name", wrapper.name)
	}

	@Test
	fun testAwareLoggerWrapperLevelChecks() {
		val logger = loggerContext.getLogger("test.aware.levels")
		logger.level = Level.TRACE
		val wrapper = AwareLoggerWrapper(logger as LocationAwareLogger)

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
	fun testAwareLoggerWrapperIsEnabled() {
		val logger = loggerContext.getLogger("test.aware.isEnabled")
		logger.level = Level.DEBUG
		val wrapper = AwareLoggerWrapper(logger as LocationAwareLogger)

		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.ERROR), true)
		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.WARN), true)
		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.INFO), true)
		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.DEBUG), true)
		assertEquals(wrapper.isEnabled(org.shypl.tool.logging.Level.TRACE), false)
	}

	@Test
	fun testAwareLoggerWrapperLogging() {
		val logger = loggerContext.getLogger("test.aware.logging")
		logger.level = Level.TRACE
		val wrapper = AwareLoggerWrapper(logger as LocationAwareLogger)

		wrapper.error("aware error msg")
		wrapper.warn("aware warn msg")
		wrapper.info("aware info msg")
		wrapper.debug("aware debug msg")
		wrapper.trace("aware trace msg")

		assertEquals(5, listAppender.list.size)
		assertEquals("aware error msg", listAppender.list[0].message)
		assertEquals(Level.ERROR, listAppender.list[0].level)
		assertEquals("aware warn msg", listAppender.list[1].message)
		assertEquals(Level.WARN, listAppender.list[1].level)
		assertEquals("aware info msg", listAppender.list[2].message)
		assertEquals(Level.INFO, listAppender.list[2].level)
		assertEquals("aware debug msg", listAppender.list[3].message)
		assertEquals(Level.DEBUG, listAppender.list[3].level)
		assertEquals("aware trace msg", listAppender.list[4].message)
		assertEquals(Level.TRACE, listAppender.list[4].level)
	}

	@Test
	fun testAwareLoggerWrapperLoggingWithThrowable() {
		val logger = loggerContext.getLogger("test.aware.throwable")
		logger.level = Level.TRACE
		val wrapper = AwareLoggerWrapper(logger as LocationAwareLogger)
		val exception = RuntimeException("aware test error")

		wrapper.error("aware error with throwable", exception)

		assertEquals(1, listAppender.list.size)
		assertEquals("aware error with throwable", listAppender.list[0].message)
		assertEquals("aware test error", listAppender.list[0].throwableProxy.message)
	}

	@Test
	fun testAwareLoggerWrapperLogMethod() {
		val logger = loggerContext.getLogger("test.aware.log")
		logger.level = Level.TRACE
		val wrapper = AwareLoggerWrapper(logger as LocationAwareLogger)

		wrapper.log(org.shypl.tool.logging.Level.ERROR, "aware log error")
		wrapper.log(org.shypl.tool.logging.Level.WARN, "aware log warn")
		wrapper.log(org.shypl.tool.logging.Level.INFO, "aware log info")
		wrapper.log(org.shypl.tool.logging.Level.DEBUG, "aware log debug")
		wrapper.log(org.shypl.tool.logging.Level.TRACE, "aware log trace")

		assertEquals(5, listAppender.list.size)
		assertEquals("aware log error", listAppender.list[0].message)
		assertEquals(Level.ERROR, listAppender.list[0].level)
		assertEquals("aware log warn", listAppender.list[1].message)
		assertEquals(Level.WARN, listAppender.list[1].level)
		assertEquals("aware log info", listAppender.list[2].message)
		assertEquals(Level.INFO, listAppender.list[2].level)
		assertEquals("aware log debug", listAppender.list[3].message)
		assertEquals(Level.DEBUG, listAppender.list[3].level)
		assertEquals("aware log trace", listAppender.list[4].message)
		assertEquals(Level.TRACE, listAppender.list[4].level)
	}
	
	@Test
	fun testAwareLoggerWrapperSyntheticDefaultLogMethod() {
		val logger = loggerContext.getLogger("test.aware.synthetic.default")
		logger.level = Level.TRACE
		val wrapper = AwareLoggerWrapper(logger as LocationAwareLogger)
		val method = AwareLoggerWrapper::class.java.getDeclaredMethod(
			"log\$default",
			AwareLoggerWrapper::class.java,
			Int::class.javaPrimitiveType,
			String::class.java,
			Throwable::class.java,
			Int::class.javaPrimitiveType,
			Any::class.java
		)
		
		method.isAccessible = true
		method.invoke(
			null,
			wrapper,
			LocationAwareLogger.INFO_INT,
			"aware synthetic default",
			RuntimeException("ignored because of default mask"),
			4,
			null
		)
		
		assertEquals(1, listAppender.list.size)
		assertEquals("aware synthetic default", listAppender.list[0].message)
		assertEquals(Level.INFO, listAppender.list[0].level)
		assertEquals(null, listAppender.list[0].throwableProxy)
	}
}
