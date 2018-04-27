package com.devskiller.jfairy.producer.unique;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueHandler<T> {
	private static final int MAX_RETRIES = 100;
	private final T producer;
	private final Set<Integer> uniqueChecksums = new HashSet<>();
	private Class<T> interfaceClass;

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

						// no need for cryptographic strength, only for collision detection and minimal
						// footprint:
						return uniqueChecksums.add(result.hashCode());
					}
				});
	}
}