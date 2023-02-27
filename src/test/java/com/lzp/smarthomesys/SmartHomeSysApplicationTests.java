package com.lzp.smarthomesys;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.lzp.smarthomesys.entity.Lock;
import com.lzp.smarthomesys.entity.Log;
import com.lzp.smarthomesys.entity.Scene;
import com.lzp.smarthomesys.service.ILogService;
import com.lzp.smarthomesys.service.impl.LockServiceImpl;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
import com.lzp.smarthomesys.service.impl.SceneServiceImpl;
import com.lzp.smarthomesys.service.impl.UserServiceImpl;
import com.lzp.smarthomesys.utils.HttpUtils;
import com.lzp.smarthomesys.utils.Token;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class SmartHomeSysApplicationTests {

//	@Test
//	void smarthomesys(){
//		FastAutoGenerator.create("jdbc:mysql://localhost:3306/smarthomesys?serverTimezone=GMT%2B8", "root", "123456")
//				.globalConfig(builder -> { // 全局配置器
//					builder.author("Bright J") // 设置作者
//							.enableSwagger() // 开启 swagger 模式
//							.fileOverride() // 覆盖已生成文件
//							.outputDir(".\\src\\main\\java"); // 指定输出目录
//				})
//				.packageConfig(builder -> { // 包配置器
//					builder.parent("com.lzp") // 设置父包名
//							.moduleName("generatortest"); // 设置父包模块名
//					// .pathInfo(Collections.singletonMap(OutputFile.xml, "D://")); // 设置mapperXml生成路径 // 默认的了
//				})
//				.strategyConfig(builder -> { // 策略配置器
//					builder.addInclude("aircon", "light", "lock", "log", "other", "room", "scene", "user") // 设置需要生成的表名
//							.addTablePrefix("t_", "c_")// 设置过滤表前缀
//							.entityBuilder() // 对于实体层的配置
//							.enableLombok() // 开启lombok
//							.enableChainModel() // 开启链式编程
//							.controllerBuilder() // 对于controller层的配置
//							.enableRestStyle(); // 使用RestCtroller
//				})
//				.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//				.execute();
//	}
//
//	@Test
//	void sendGetUtil() throws IOException {
//		String result = HttpUtils.sendGet("http://47.92.153.40:8080/light/queryAll", null, null);
//		System.out.println(result);
//	}
//
//	@Test
//	void sendPostUtil_Map(){
//		Map<String,String> map = new HashMap<>();
//		map.put("brand", "鹏哥电器");
//		map.put("power", "30");
//		map.put("remark", "五楼卧室主灯");
//		map.put("state", "关");
//		map.put("serviceTime", "1000");
//		String result = HttpUtils.sendPost("http://47.92.153.40:8080/light/add",null, map);
//		System.out.println(result);
//	}
//
//	@Test
//	void sendPostUtil_Json(){
//		Map<String,String> map = new HashMap<>();
//		map.put("brand", "鹏哥电器");
//		map.put("power", "30");
//		map.put("remark", "六楼卧室主灯");
//		map.put("state", "关");
//		map.put("serviceTime", "1000");
//		String result = HttpUtils.sendPostJson("http://47.92.153.40:8080/light/add", null, map);
//		System.out.println(result);
//	}
//
//	@Autowired
//	LogServiceImpl service;
//
//	@Test
//	void logTest001(){
//		List<Log> list = service.list();
//		list.forEach(System.out::println);
//	}
//
//	@Test
//	void logTest002(){
//		Log log = new Log();
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String time = df.format(System.currentTimeMillis());
//		log.setTime(time);
//		log.setTarget("客厅左边空调456");
//		log.setAction("开启");
//		boolean save = service.save(log);
//		System.out.println("save = " + save);
//	}
//
//	@Test
//	void logTest003(){
//		boolean save = service.removeById("1");
//		System.out.println("save = " + save);
//	}
//
//	@Autowired
//	UserServiceImpl userService;
//
//	@Test
//	void userTest001(){
//		boolean b = userService.removeById(1);
//
//		System.out.println("b = " + b);
//	}
//
//	@Resource
//	LockServiceImpl lockService;
//
//	@Test
//	void lockTest001(){
//		LambdaQueryWrapper<Lock> wrapper = new LambdaQueryWrapper<>();
//		wrapper.eq(Lock::getRoomId, "2");
//		List<Lock> list = lockService.list(wrapper);
//		list.forEach(System.out::println);
//	}
//
//    @Test
//    public void tokenTest() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
//        String token = Token.getToken();
//        System.out.println("token = " + token);
//    }
//
//
//    @Resource
//    SceneServiceImpl sceneService;
//
//    @Test
//    public void test0001(){
//        Scene scene = sceneService.getById("1629310009525506049");
//        String appliance = scene.getAppliance();
//
//        String[] all = appliance.split(";");
////
////        for (String temp : all) {
////            String[] single = temp.split(",");
////            System.out.println(single[0] + "|" + single[1]);
////        }
//
//        Map<String, String> res = new IdentityHashMap<>();
//
//        for (String temp : all) {
//            String[] single = temp.split(",");
//            res.put(single[0], single[1]);
//        }
//
//        System.out.println(res);
////        res.forEach((s, o) -> System.out.println(s + ":" + o));
//
////        Map<String, String> aircon = new HashMap<>();
////        Map<String, String> light = new HashMap<>();
////        Map<String, String> lock = new HashMap<>();
////        Map<String, String> log = new HashMap<>();
////        Map<String, String> other = new HashMap<>();
//    }
}
