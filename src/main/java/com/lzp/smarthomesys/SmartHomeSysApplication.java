package com.lzp.smarthomesys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lzp.smarthomesys.mapper") // 扫描mapper文件的位置
@EnableScheduling // 开启定时任务
public class SmartHomeSysApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartHomeSysApplication.class, args);
	}

}
