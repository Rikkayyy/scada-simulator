package com.energyprojects.scada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScadaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScadaBackendApplication.class, args);
	}

}
