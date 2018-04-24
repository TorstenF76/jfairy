/*
 * Copyright (c) 2018. Codearte
 */

package com.devskiller.jfairy.producer;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.devskiller.jfairy.producer.text.TextProducer;

@Singleton
public class MagicProducer {

	private final BaseProducer baseProducer;
	private final TextProducer textProducer;
	private final DateProducer dateProducer;

	@Inject
	public MagicProducer(BaseProducer baseProducer, TextProducer textProducer, DateProducer dateProducer) {
		this.baseProducer = baseProducer;
		this.textProducer = textProducer;
		this.dateProducer = dateProducer;
	}

	/**
	 * apply random values to the fields with the given names. If no field names are
	 * given, then all fields will be tried to be injected where possible.
	 * 
	 * @param o
	 *            the object where the random values shall be injected
	 * @param fieldNames
	 *            the names of the fields that shall be injected
	 */
	public void bewitch(Object o, String... fieldNames) {
		if (o == null) {
			// callers do not need to check for null objects
			return;
		}

		boolean fieldsAreGiven = isNotEmpty(fieldNames);

		getFields(o, fieldNames).forEach(field -> bewitchField(o, field, fieldsAreGiven));
	}

	/**
	 * get fields of the object. If no fieldNames are given, all fields will be returned
	 * @param o
	 * @param fieldNames
	 * @return
	 */
	private List<Field> getFields(Object o, String... fieldNames) {
		if (isEmpty(fieldNames)) {
			return asList(o.getClass().getDeclaredFields());
		} else {
			return asList(fieldNames).stream().map(fn -> getField(o, fn)).collect(toList());
		}
	}

	/**
	 * wrap getClass().getDeclaredField(String) without checked exception
	 * @param o
	 * @param fieldName
	 * @return
	 */
	private Field getField(Object o, String fieldName) {
		try {
			return o.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException ex) {
			throw new IllegalArgumentException("field not found: " + fieldName, ex);
		}
	}

	/**
	 * prepare field and revert preparation afterwards
	 * 
	 * @param o
	 *            the object to bewitch
	 * @param fieldName
	 *            the field to bewitch
	 * @param rethrowExceptions
	 *            rethrow exceptions when fields cannot be set
	 */
	private void bewitchField(Object o, Field field, boolean rethrowExceptions) {
		Boolean isAccessible = null;
		try {
			isAccessible = field.isAccessible();

			if (!isAccessible) {
				field.setAccessible(true);
			}

			bewitchField(o, field);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | IllegalStateException e) {
			if (rethrowExceptions) {
				throw new IllegalArgumentException(
						"could not bewitch field " + field.getName() + " of class " + o.getClass().getSimpleName(), e);
			}
		} finally {
			// restore original state
			if (isAccessible == Boolean.FALSE) {
				field.setAccessible(isAccessible);
			}
		}
	}

	private void bewitchField(Object o, Field field) throws IllegalArgumentException, IllegalAccessException {
		if (String.class.isAssignableFrom(field.getType())) {
			field.set(o, textProducer.word());
		} else if (Number.class.isAssignableFrom(field.getType())) {
			bewitchNumber(o, field);
		} else if (Date.class.isAssignableFrom(field.getType())) {
			field.set(o, Date.from(randomDate().toInstant(ZoneOffset.UTC)));
		} else if (Temporal.class.isAssignableFrom(field.getType())) {
			bewitchTemporal(o, field);
		} else {
			throw new IllegalStateException("cannot bewitch field " + field.getName() + " of type " + field.getType());
		}
	}

	private void bewitchNumber(Object o, Field field) throws IllegalAccessException {
		if (Long.class.isAssignableFrom(field.getType())) {
			field.set(o, baseProducer.randomBetween(Long.MIN_VALUE, Long.MAX_VALUE));
		} else if (Integer.class.isAssignableFrom(field.getType())) {
			field.set(o, baseProducer.randomBetween(Integer.MIN_VALUE, Integer.MAX_VALUE));
		} else if (Double.class.isAssignableFrom(field.getType())) {
			field.set(o, baseProducer.randomBetween(Double.MIN_VALUE, Double.MAX_VALUE));
		} else if (Float.class.isAssignableFrom(field.getType())) {
			field.set(o, new Float(baseProducer.randomBetween(Float.MIN_VALUE, Float.MAX_VALUE)));
		} else if (BigInteger.class.isAssignableFrom(field.getType())) {
			field.set(o, BigInteger.valueOf(baseProducer.randomBetween(Long.MIN_VALUE, Long.MAX_VALUE)));
		} else if (BigDecimal.class.isAssignableFrom(field.getType())) {
			field.set(o, BigDecimal.valueOf(baseProducer.randomBetween(Double.MIN_VALUE, Double.MAX_VALUE)));
		} else {
			throw new IllegalStateException("cannot bewitch number " + field.getName() + " of type " + field.getType());
		}
	}

	private void bewitchTemporal(Object o, Field field) throws IllegalAccessException {
		if (LocalDateTime.class.isAssignableFrom(field.getType())) {
			field.set(o, randomDate());
		} else if (ZonedDateTime.class.isAssignableFrom(field.getType())) {
			field.set(o, randomDate().atZone(ZoneId.systemDefault()));
		} else if (Instant.class.isAssignableFrom(field.getType())) {
			field.set(o, randomDate().toInstant(ZoneOffset.UTC));
		} else {
			throw new IllegalStateException(
					"cannot bewitch temporal " + field.getName() + " of type " + field.getType());
		}
	}

	private LocalDateTime randomDate() {
		return dateProducer.randomDateBetweenYears(2000,  2100);
	}
}
