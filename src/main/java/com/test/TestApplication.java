package com.test;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication {

	public static void main(String[] args) {
		String port = System.getenv("PORT");
		if (port == null) {
			System.out.println("$PORT environment variable not set");
		}
		SpringApplication app = new SpringApplication(TestApplication.class);

		app.setDefaultProperties(Collections.singletonMap("server.port", "8080"));
		app.run(args);

	}

}
