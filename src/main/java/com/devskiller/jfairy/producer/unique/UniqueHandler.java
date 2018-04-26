package com.devskiller.jfairy.producer.unique;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

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
						Object result = null;
						boolean isUnique = false;
						int retryCounter = 0;
						do {
							// invoke real instance:
							result = method.invoke(producer, args);
							isUnique = isUnique(result);
							retryCounter++;
							if (retryCounter >= MAX_RETRIES) {
								// prevent endless loop if no more elements are possible to create
								throw new IllegalStateException("no more unique element found after " + retryCounter
										+ " retries. Last found element was " + result);
							}
						} while (isUnique == false);
						return result;
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