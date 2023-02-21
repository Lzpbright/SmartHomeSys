package com.lzp.smarthomesys;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.lzp.smarthomesys.utils.HttpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SmartHomeSysApplicationTests {

	@Test
	void generatorTest(){
		FastAutoGenerator.create("jdbc:mysql://localhost:3306/mybatis?serverTimezone=GMT%2B8", "root", "123456")
				.globalConfig(builder -> { // 全局配置器
					builder.author("Bright J") // 设置作者
							.enableSwagger() // 开启 swagger 模式
							.fileOverride() // 覆盖已生成文件
							.outputDir(".\\src\\main\\java"); // 指定输出目录
				})
				.packageConfig(builder -> { // 包配置器
					builder.parent("com.lzp") // 设置父包名
							.moduleName("generatortest"); // 设置父包模块名
					// .pathInfo(Collections.singletonMap(OutputFile.xml, "D://")); // 设置mapperXml生成路径 // 默认的了
				})
				.strategyConfig(builder -> { // 策略配置器
					builder.addInclude("t_account") // 设置需要生成的表名
							.addTablePrefix("t_", "c_")// 设置过滤表前缀
							.entityBuilder() // 对于实体层的配置
							.enableLombok() // 开启lombok
							.enableChainModel() // 开启链式编程
							.controllerBuilder() // 对于controller层的配置
							.enableRestStyle(); // 使用RestCtroller
				})
				.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
				.execute();
	}

	@Test
	void sendGetUtil() throws IOException {
		String result = HttpUtils.sendGet("http://47.92.153.40:8080/light/queryAll", null, null);
		System.out.println(result);
	}

	@Test
	void sendPostUtil_Map(){
		Map<String,String> map = new HashMap<>();
		map.put("brand", "鹏哥电器");
		map.put("power", "30");
		map.put("remark", "五楼卧室主灯");
		map.put("state", "关");
		map.put("serviceTime", "1000");
		String result = HttpUtils.sendPost("http://47.92.153.40:8080/light/add",null, map);
		System.out.println(result);
	}

	@Test
	void sendPostUtil_Json(){
		Map<String,String> map = new HashMap<>();
		map.put("brand", "鹏哥电器");
		map.put("power", "30");
		map.put("remark", "六楼卧室主灯");
		map.put("state", "关");
		map.put("serviceTime", "1000");
		String result = HttpUtils.sendPostJson("http://47.92.153.40:8080/light/add", null, map);
		System.out.println(result);
	}


}
