package org.shypl.tool.logging

import kotlin.test.Test
import kotlin.test.assertEquals

class TestLogging {
	
	@Test
	fun getLoggerPassesNoErrors() {
		Logging.getLogger("foo.bar")
		Logging.getLogger(StubClass::class)
		Logging.getLogger<StubClass>()
	}
	
	@Test
	fun getLoggerByNameReturnsEqualObjects() {
		val a = Logging.getLogger("foo.bar")
		val b = Logging.getLogger("foo.bar")
		
		assertEquals(a, b)
	}
	
	@Test
	fun getLoggerByClassReturnsEqualObjects() {
		val a = StubClass().logger
		val b = Logging.getLogger<StubClass>()
		
		assertEquals(a, b)
	}
	
	@Test
	fun getLoggerByClassAndNameReturnsEqualObjects() {
		val a = StubClass().logger
		val b = Logging.getLogger("org.shypl.tool.logging.StubClass")
		
		assertEquals(a, b)
	}
	
	@Test
	fun getLoggerByOwnAndNameReturnsEqualObjects() {
		val a = StubClass().ownLogger
		val b = Logging.getLogger("org.shypl.tool.logging.StubClass")
		
		assertEquals(a, b)
	}
}