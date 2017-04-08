package com.example.netty.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.aop.support.AopUtils;

public class ClassUtils {

	public static Class<?> getSuperClassGenericClass(Class<?> clazz, int index) {
		Class<?> targetClass = AopUtils.getTargetClass(clazz);
		
		Type type = targetClass.isInterface() ? targetClass.getGenericInterfaces()[0] : targetClass.getGenericSuperclass();
		
		ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        
        return (Class<?>) typeArguments[index];
	}
}
