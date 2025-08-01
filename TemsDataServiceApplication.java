package com.tridel.tems_data_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class TemsDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemsDataServiceApplication.class, args);
	}

}
