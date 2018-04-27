package com.devskiller.jfairy.producer.unique;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class UniqueHandler<T> {
	private static final int MAX_RETRIES = 100;
	private final T producer;
	private final Set<String> uniqueChecksums = new HashSet<>();
	private Class<T> interfaceClass;
	private static final HashFunction hashFunction = Hashing.goodFastHash(64);

	/**
	 * 
	 * @param producer
	 * @param interfaceClass
	 *            we need an interface to create the proxy. the class of the
	 *            producer is not the interface
	 */
	public UniqueHandler(T producer, Class<T> interfaceClass) {
		this.producer = producer;
		this.interfaceClass = interfaceClass;
	}

	@SuppressWarnings("unchecked")
	public T createProxy() {
		return (T) Proxy.newProxyInstance(producer.getClass().getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						int retryCounter = MAX_RETRIES;
						do {
							// invoke real instance:
							Object result = method.invoke(producer, args);

							// check for forbidden methods
							if (method.isAnnotationPresent(UniqueIgnore.class)) {
								return result;
							}

							// check for uniqueness
							if (isUnique(result)) {
								return result;
							}

							// try again
							retryCounter--;
						} while (retryCounter > 0);

						// prevent endless loop if no more elements are possible to create
						throw new IllegalStateException(
								"no more unique element found after " + MAX_RETRIES + " retries. ");
					}

					private boolean isUnique(Object result) {
						// null values are no values, so they are not checked
						if (result == null) {
							return true;
						}

						HashCode hashCode = createHashcode(result);
						return uniqueChecksums.add(hashCode.toString());
					}

					/**
					 * no need for cryptographic strength, only for collision detection and minimal
					 * footprint
					 */
					private HashCode createHashcode(Object result) {
						if (result instanceof Long) {
							return hashFunction.hashLong((Long) result);
						}
						if (result instanceof Integer) {
							return hashFunction.hashInt((Integer) result);
						}
						if (result instanceof String) {
							return hashFunction.hashString((String) result, StandardCharsets.UTF_8);
						}
						// everything else should have a meaningful toString() implementation
						return hashFunction.hashString(result.toString(), StandardCharsets.UTF_8);
					}
				});
	}
}