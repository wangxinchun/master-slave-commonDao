package com.job.db.dataservice.exception;

/**
 * Result 和 Bean 转换异常
 * @author wangxinchun1988@163.com
 */
public class BeanException extends RuntimeException {
    
	private static final long serialVersionUID = 5870969193725605713L;

	public BeanException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public BeanException(String msg) {
        super(msg);
    }
}
