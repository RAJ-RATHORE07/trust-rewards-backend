package com.fintech.trust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class TrustScoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrustScoreServiceApplication.class, args);
	}
}
