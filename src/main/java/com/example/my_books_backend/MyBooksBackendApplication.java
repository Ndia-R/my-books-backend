package com.example.my_books_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MyBooksBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyBooksBackendApplication.class, args);
	}

}
