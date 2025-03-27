package com.example.GridFSDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GridFsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GridFsDemoApplication.class, args);
	}

}

//Can accept photo uploads, stores to db with title
//Code source -> https://www.baeldung.com/spring-boot-mongodb-upload-file