package com.job.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.job.db.dao.CommonDao;

/**
 * test case
 * @author wangxinchun1988@163.com
 * @date 2014-7-16下午5:38:54
 */
@Controller
public class TestController {
	@Autowired
    private CommonDao commonDao;
	
	@RequestMapping("/login")
	public void test(HttpServletRequest request,HttpServletResponse response){
		long ret = commonDao.insert("INSERT INTO client_shard 	(HOST, 	PORT, 	databaseName, 	clientId, 	config, 	order_prefix	)	VALUES	('sss', 	'bbb', 	'ddd', 	'ddd', 	'1', 	'aa'	);", "tts");
		System.out.println(ret);
	}
}
