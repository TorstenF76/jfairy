/*
 * Copyright (c) 2013 Codearte and authors
 */
package com.devskiller.jfairy.producer

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime

import com.devskiller.jfairy.Fairy
import com.devskiller.jfairy.producer.text.TextProducer

import groovy.transform.ToString
import spock.lang.Specification

class MagicProducerSpec extends Specification {

	private static final int MAX_YEARS_IN_THE_PAST = 5

	private static final LocalDateTime CURRENT_DATE = LocalDateTime.parse("2013-11-09T01:16:00")
	private static final int CURRENT_YEAR = 2013

	private static final String RANDOM_WORD = UUID.randomUUID().toString()
	private static final Fairy fairy = Fairy.create();
	private MagicProducer sut = fairy.magicProducer();
	private TestClass testObject = new TestClass()

	@ToString(includeFields = true, includeNames = true)
	private static class TestClass {
		public String publicString;
		private String privateString;
		Integer anInteger;
		Float aFloat;
		Double aDouble;
		BigInteger aBigInt;
		Instant anInstant;
		LocalDateTime aLocalDateTime;
		ZonedDateTime aZonedDateTime;
	}

	def cleanup() {
		// debug output to have a look at the randomly generated values 
		println testObject
	}
	
	def "should generate string public and private"() {
		when:
			sut.bewitch(testObject, "publicString", "privateString")
		then:
			testObject.publicString != null
			testObject.privateString != null
	}

	def "should generate numbers"() {
		when:
			sut.bewitch(testObject, "anInteger", "aFloat", "aDouble", "aBigInt")
		then:
			testObject.anInteger != null
			testObject.aFloat != null
			testObject.aDouble != null
			testObject.aBigInt != null
	}

	def "should generate java time stuff"() {
		when:
			sut.bewitch(testObject, "anInstant", "aLocalDateTime", "aZonedDateTime")
		then:
			testObject.anInstant != null
			testObject.aLocalDateTime != null
			testObject.aZonedDateTime != null
	}
	
	def "should generate everything automatically"() {
		when:
			sut.bewitch(testObject)
		then:
			testObject.publicString != null
			testObject.privateString != null
			testObject.anInteger != null
			testObject.aFloat != null
			testObject.aDouble != null
			testObject.aBigInt != null
			testObject.anInstant != null
			testObject.aLocalDateTime != null
			testObject.aZonedDateTime != null
	}
	
}
