package com.job.db.dao;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试case
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午5:42:43
 */
@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations = {"classpath*:spring/applicationContext*.xml"  ,
      "file:src/main/resources/spring/servlet/appServlet-context.xml"})  
public class CommonDaoTest {
	@Autowired
	private CommonDao commonDao;
	/**
	 * slave 查询数据
	 */
	@Test
	public  void testDAOFindMap() {
		List<Map<String, Object>> ret = commonDao.findListMap("select * from client_shard where id = 30", "tts");
		System.out.println(ret);
	}
	
	
	/**
	 * Master delete 数据 
	 */
	@Test
	public  void testDelete() {
		long ret = commonDao.insert("INSERT INTO client_shard 	(HOST, 	PORT, 	databaseName, 	clientId, 	config, 	order_prefix	)	VALUES	('sss', 	'bbb', 	'ddd', 	'ddd', 	'1', 	'aa'	);", "tts");
		System.out.println(ret);
	}
	
	/**Master insert 数据 */
	@Test
	public  void testInsert() {
		long ret = commonDao.insert("INSERT INTO client_shard 	(HOST, 	PORT, 	databaseName, 	clientId, 	config, 	order_prefix	)	VALUES	('sss', 	'bbb', 	'ddd', 	'ddd', 	'1', 	'aa'	);", "tts");
		System.out.println(ret);
	}
}
