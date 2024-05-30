package com.youngpotato.foodluv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FoodLuvBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodLuvBackApplication.class, args);
	}

}
