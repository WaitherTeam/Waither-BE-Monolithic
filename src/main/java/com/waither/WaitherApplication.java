package com.waither;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WaitherApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaitherApplication.class, args);
	}

}
