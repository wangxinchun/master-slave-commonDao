package com.job.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 2010-4-15
 * Time: 15:05:05
 *
 * callback interface
 */
public interface IResultGetter {

    public Object getResultFormRs(ResultSet rs) throws SQLException;
}
