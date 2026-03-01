package org.shypl.tool.logging

import org.shypl.tool.logging.Level as LogLevel
import kotlin.test.Test
import kotlin.test.assertEquals

class LevelTest {
	@Test
	fun testValues() {
		assertEquals(5, LogLevel.entries.size)
		assertEquals(LogLevel.ERROR, LogLevel.valueOf("ERROR"))
		assertEquals(LogLevel.WARN, LogLevel.valueOf("WARN"))
		assertEquals(LogLevel.INFO, LogLevel.valueOf("INFO"))
		assertEquals(LogLevel.DEBUG, LogLevel.valueOf("DEBUG"))
		assertEquals(LogLevel.TRACE, LogLevel.valueOf("TRACE"))
	}

	@Test
	fun testOrdinal() {
		assertEquals(0, LogLevel.ERROR.ordinal)
		assertEquals(1, LogLevel.WARN.ordinal)
		assertEquals(2, LogLevel.INFO.ordinal)
		assertEquals(3, LogLevel.DEBUG.ordinal)
		assertEquals(4, LogLevel.TRACE.ordinal)
	}
}
