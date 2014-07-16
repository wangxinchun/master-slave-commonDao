package com.job.db.utils;

import java.lang.reflect.Method;

/**
 * @author wangxinchun1988@163.com
 */
public class BeanProperty {
	
	private Class<?> writeParamentType;
	private Method writeMethod;

	public void setWriteMethod(Method method) {
		this.writeMethod = method;
		if (method != null) {
			writeParamentType = method.getParameterTypes()[0];
		}
	}

	public Class<?> getWriteParamentType() {
		return this.writeParamentType;
	}

	public Method getWriteMethod() {
		return writeMethod;
	}

}
