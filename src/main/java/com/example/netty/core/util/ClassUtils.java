package com.example.netty.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtils {

	public static Class<?> getInterfaceGenericType(Class<?> clazz, int index) {
		Type type = clazz.getGenericInterfaces()[0];
		
		ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        
        return (Class<?>) typeArguments[index];
	}
}
