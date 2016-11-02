package com.waiwang1113.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starting point for application, @SpringBootApplication annotation is used to 
 * trigger bootstrap process
 * @author wanwe17
 *
 */
@SpringBootApplication 
public class MainApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);

	}
}
