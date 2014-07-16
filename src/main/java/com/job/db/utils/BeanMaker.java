package com.job.db.utils;

import com.job.db.dataservice.exception.BeanException;

/**
 * 根据Class 生成实例
 * @author wangxinchun1988@163.com
 */
public abstract class BeanMaker {

	public static <T> T makeBeanByClass(Class<T> clz) {
        try {
			T ob = clz.newInstance();
			return ob;
		} catch (Exception e) {
            throw new BeanException("make bean instance error " + clz, e);
        }
    }

	public static <T> T makeBeanByName(String name) {
        try {
			@SuppressWarnings("unchecked")
			Class<T> clz = (Class<T>) Class.forName(name);
			T ob = makeBeanByClass(clz);
			return ob;
		} catch (Exception e) {
            throw new BeanException("make bean instance error " + name, e);
        }
	}

    }
