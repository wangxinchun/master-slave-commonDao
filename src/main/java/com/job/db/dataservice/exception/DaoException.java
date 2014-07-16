package com.job.db.dataservice.exception;


/**
 * @author wangxinchun1988@163.com
 */
public class DaoException extends RuntimeException {

	private static final long serialVersionUID = -1743564252200244089L;

	public DaoException(String msg, Throwable e) {
        super(msg, e);
    }
	public DaoException(String msg) {
        super(msg);
    }
}
