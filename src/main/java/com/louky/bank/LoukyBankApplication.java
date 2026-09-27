package com.louky.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan

public class LoukyBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoukyBankApplication.class, args);
	}

}
