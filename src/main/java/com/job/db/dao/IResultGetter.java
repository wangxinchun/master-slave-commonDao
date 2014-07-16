package com.job.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 结果集数据回调
 * @author wangxinchun1988@163.com
 */
public interface IResultGetter {

    public Object getResultFormRs(ResultSet rs) throws SQLException;
}
