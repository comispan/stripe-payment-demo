package com.example.payment_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaymentDemoApplication {

	static void main(String[] args) {
		SpringApplication.run(PaymentDemoApplication.class, args);
	}

}
