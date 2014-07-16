package com.job.db.dataservice.exception;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 2010-4-14
 * Time: 14:29:07
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
