package com.ThalahenaPublicLibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Enables @Scheduled annotation for tasks like reservation expiry
public class ThalahenaPublicLibrarydemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThalahenaPublicLibrarydemoApplication.class, args);
	}
}
