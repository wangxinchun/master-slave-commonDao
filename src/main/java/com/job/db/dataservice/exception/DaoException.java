package com.job.db.dataservice.exception;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 2010-4-15
 * Time: 15:17:44
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
