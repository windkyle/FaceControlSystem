package com.xlauncher.fis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Administrator
 */
@SpringBootApplication
@MapperScan("com.xlauncher.fis.dao")
public class FisApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(FisApplication.class, args);
	}

}
